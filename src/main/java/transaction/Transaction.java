package transaction;

import amount.Amount;

import java.time.LocalDateTime;
import java.util.UUID;

public record Transaction(UUID accountId, LocalDateTime date, Amount amount, Amount balanceAfterExecution, TypeTransaction type) {
}
