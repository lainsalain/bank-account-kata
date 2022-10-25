package transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionsDAO {
    Transaction save(UUID accountId, Transaction transaction);
    Optional<Transaction> findLast(UUID accountId);


}
