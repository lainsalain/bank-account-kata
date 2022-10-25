package transaction;

import amount.Amount;
import exceptions.NegativeAmountException;
import exceptions.NotEnoughMoneyException;
import utils.StatementPrinter;
import utils.TransactionFormatter;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static amount.Amount.amountOf;

public class TransactionService {

    private final TransactionsDAO transactionsDAO;
    private final Clock clock;
    private final TransactionFormatter formatter;
    private final StatementPrinter printer;

    public TransactionService(TransactionsDAO transactionsDAO, Clock clock, TransactionFormatter formatter, StatementPrinter printer) {
        this.transactionsDAO = transactionsDAO;
        this.clock = clock;
        this.formatter = formatter;
        this.printer = printer;
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

    public void printStatement(UUID accountId) {
        final List<Transaction> transactions = transactionsDAO.findByAccountId(accountId);
        final List<String> formattedTransactions = formatter.format(transactions);
        printer.print(formattedTransactions);
    }

}
