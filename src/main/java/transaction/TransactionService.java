package transaction;

import amount.Amount;
import exceptions.NegativeAmountException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionService {

    private final TransactionsDAO transactionsDAO;
    private final Clock clock;


    public TransactionService(TransactionsDAO transactionsDAO, Clock clock) {
        this.transactionsDAO = transactionsDAO;
        this.clock = clock;
    }

    public Transaction deposit(UUID accountId, Amount amount) throws NegativeAmountException {
        Transaction deposit = new Transaction(accountId, LocalDateTime.now(clock), amount, amount, TypeTransaction.DEPOSIT);
        return this.transactionsDAO.save(accountId, deposit);
    }

}
