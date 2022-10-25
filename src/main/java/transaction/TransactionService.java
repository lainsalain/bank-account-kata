package transaction;

import amount.Amount;
import exceptions.NegativeAmountException;
import exceptions.NotEnoughMoneyException;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import static amount.Amount.amountOf;

public class TransactionService {

    private final TransactionsDAO transactionsDAO;
    private final Clock clock;


    public TransactionService(TransactionsDAO transactionsDAO, Clock clock) {
        this.transactionsDAO = transactionsDAO;
        this.clock = clock;
    }

    public Transaction deposit(UUID accountId, Amount amount) throws NegativeAmountException {
        LocalDateTime date = LocalDateTime.now(clock);
        Amount currentBalance = getCurrentBalance(accountId);

        Transaction deposit = new Transaction(accountId, date, amount, currentBalance.plus(amount), TypeTransaction.DEPOSIT);
        this.transactionsDAO.save(accountId, deposit);
        return deposit;
    }

    public Transaction withdrawal(UUID accountId, Amount amount) throws NegativeAmountException, NotEnoughMoneyException {
        LocalDateTime date = LocalDateTime.now(clock);
        Amount currentBalance = getCurrentBalance(accountId);
        if(amount.compareTo(currentBalance) > 0) throw new NotEnoughMoneyException();

        Transaction withdrawal = new Transaction(accountId, date, amount, currentBalance.minus(amount), TypeTransaction.WITHDRAWAL);
        this.transactionsDAO.save(accountId, withdrawal);
        return withdrawal;
    }

    private Amount getCurrentBalance(UUID accountId) throws NegativeAmountException {
        return transactionsDAO.findLast(accountId)
                .map(Transaction::balanceAfterExecution)
                .orElse(amountOf(BigDecimal.ZERO));
    }

}
