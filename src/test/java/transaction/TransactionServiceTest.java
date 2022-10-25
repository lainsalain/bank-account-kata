package transaction;

import amount.Amount;
import exceptions.NegativeAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static amount.Amount.amountOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private static final Clock clock = Clock.fixed(Instant.ofEpochSecond(3600), ZoneId.systemDefault());
    @Mock
    private TransactionsDAO transactionsDAO;
    private TransactionService transactionService;


    @BeforeEach
    void setUp() {
        this.transactionService = new TransactionService(transactionsDAO, clock);
    }

    @Test
    @DisplayName("Should deposit the amount parameter in the bank account and return the deposit")
    void shouldDepositAmountInBankAccount() throws NegativeAmountException {
        final UUID accountId = UUID.randomUUID();
        final LocalDateTime expectedDate = LocalDateTime.now(clock);
        final Amount expectedBalance = amountOf(new BigDecimal("30.00"));

        final Transaction expectedTransaction = new Transaction(accountId, expectedDate, amountOf(new BigDecimal("10.00")), expectedBalance, TypeTransaction.DEPOSIT);
        when(transactionsDAO.findLast(accountId))
                .thenReturn(Optional
                        .of(new Transaction(accountId, LocalDateTime.now(clock), amountOf(new BigDecimal("20.00")), amountOf(new BigDecimal("20.00")), TypeTransaction.DEPOSIT)));

        Transaction foundTransaction = transactionService.deposit(accountId, amountOf(new BigDecimal("10.00")));

        assertEquals(expectedTransaction, foundTransaction);

        InOrder orderVerifier = inOrder(transactionsDAO);
        orderVerifier.verify(this.transactionsDAO).findLast(accountId);
        orderVerifier.verify(this.transactionsDAO).save(accountId, foundTransaction);
        orderVerifier.verifyNoMoreInteractions();

    }

    @Test
    @DisplayName("Should deposit the amount parameter in the bank account and return the deposit")
    void shouldNotDepositAmountInBankAccountBecauseOfNegativeDeposit() {
        final UUID accountId = UUID.randomUUID();
        final Exception exception = assertThrows(NegativeAmountException.class,
                () -> transactionService.deposit(accountId, amountOf(new BigDecimal("-10.00"))));

        assertEquals("The amount value cannot be negative.", exception.getMessage());
        verifyZeroInteractions(transactionsDAO);
    }
}