package transaction;

import amount.Amount;
import exceptions.NegativeAmountException;
import exceptions.NotEnoughMoneyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.StatementPrinter;
import utils.TransactionFormatter;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

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
    @Mock
    private TransactionFormatter transactionFormatter;
    @Mock
    private StatementPrinter statementPrinter;



    @BeforeEach
    void setUp() {
        this.transactionService = new TransactionService(transactionsDAO, clock, transactionFormatter, statementPrinter);
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

    @Test
    @DisplayName("Should withdrawal the amount parameter out of the bank account and return the withdrawal")
    void shouldWithdrawalAmountOutOfBankAccount() throws NotEnoughMoneyException, NegativeAmountException {
        final UUID accountId = UUID.randomUUID();
        final LocalDateTime expectedDate = LocalDateTime.now(clock);
        final Amount expectedAmount = amountOf(new BigDecimal("10.00"));
        final Amount expectedBalance = amountOf(new BigDecimal("20.00"));

        final Transaction expectedTransaction = new Transaction(accountId, expectedDate, expectedAmount, expectedBalance, TypeTransaction.WITHDRAWAL);
        when(transactionsDAO.findLast(accountId))
                .thenReturn(Optional
                        .of(new Transaction(accountId, LocalDateTime.now(clock), amountOf(new BigDecimal("5.00")), amountOf(new BigDecimal("30.00")), TypeTransaction.WITHDRAWAL)));

        Transaction foundTransaction = transactionService.withdrawal(accountId, amountOf(new BigDecimal("10.00")));

        assertEquals(expectedTransaction, foundTransaction);
        InOrder orderVerifier = inOrder(transactionsDAO);
        orderVerifier.verify(this.transactionsDAO).findLast(accountId);
        orderVerifier.verify(this.transactionsDAO).save(accountId, foundTransaction);
        orderVerifier.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @MethodSource("transactionsProvider")
    @DisplayName("Should return NotEnoughMoneyException when withdrawal is called")
    void shouldReturnNotEnoughMoneyExceptionWhenWithdrawalIsCalled(Optional<Transaction> transaction) {
        final UUID accountId = UUID.randomUUID();
        when(transactionsDAO.findLast(accountId)).thenReturn(transaction);

        final Exception exception = assertThrows(NotEnoughMoneyException.class,
                () -> transactionService.withdrawal(accountId, amountOf(new BigDecimal("10.00"))));

        assertEquals("The withdrawal cannot be greater than the balance.", exception.getMessage());
        verify(this.transactionsDAO).findLast(accountId);
        verifyNoMoreInteractions(transactionsDAO);
    }

    private static Stream<Arguments> transactionsProvider() throws NegativeAmountException {
        return Stream.of(
                Arguments.of(
                        Optional.of(new Transaction(
                                UUID.randomUUID(),
                                LocalDateTime.now(clock),
                                amountOf(new BigDecimal("50")),
                                amountOf(new BigDecimal("5.00")),
                                TypeTransaction.WITHDRAWAL
                        ))),
                Arguments.of(Optional.<Transaction>empty())
        );
    }

    @Test
    @DisplayName("Should print all transactions from the account id")
    void shouldPrintAllTransactionsFromAccountId() throws NegativeAmountException {
        final UUID accountId = UUID.randomUUID();
        final List<Transaction> transactions = List.of(
                new Transaction(accountId, LocalDateTime.now(clock), amountOf(new BigDecimal("100.00")), amountOf(new BigDecimal("50.00")), TypeTransaction.DEPOSIT)
        );
        when(transactionsDAO.findByAccountId(accountId)).thenReturn(transactions);
        final List<String> formattedTransactions = List.of("List of transactions");
        when(transactionFormatter.format(transactions)).thenReturn(formattedTransactions);

        transactionService.printStatement(accountId);

        InOrder verifyOrder = inOrder(transactionsDAO, transactionFormatter, statementPrinter);
        verifyOrder.verify(transactionsDAO).findByAccountId(accountId);
        verifyOrder.verify(transactionFormatter).format(transactions);
        verifyOrder.verify(statementPrinter).print(formattedTransactions);
        verifyOrder.verifyNoMoreInteractions();
    }
}