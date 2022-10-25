package utils;

import exceptions.NegativeAmountException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import transaction.Transaction;
import transaction.TypeTransaction;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static amount.Amount.amountOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TableTransactionFormatterTest {
    private final Clock clock = Clock.fixed(Instant.ofEpochSecond(3600), ZoneId.systemDefault());

    @Test
    @DisplayName("Should format the history of the transactions")
    void shouldFormatHistoryOfTransactions() throws NegativeAmountException {
        TableTransactionFormatter table = new TableTransactionFormatter();
        final UUID accountId = UUID.randomUUID();
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(accountId, LocalDateTime.now(clock),amountOf(new BigDecimal("10.00")), amountOf(new BigDecimal("465.00")), TypeTransaction.DEPOSIT));
        transactions.add(new Transaction(accountId, LocalDateTime.now(clock),amountOf(new BigDecimal("30.00")), amountOf(new BigDecimal("455.00")), TypeTransaction.WITHDRAWAL));
        transactions.add(new Transaction(accountId, LocalDateTime.now(clock),amountOf(new BigDecimal("15.00")), amountOf(new BigDecimal("485.00")), TypeTransaction.DEPOSIT));
        transactions.add(new Transaction(accountId, LocalDateTime.now(clock),amountOf(new BigDecimal("42.00")), amountOf(new BigDecimal("470.00")), TypeTransaction.DEPOSIT));


        final var expectedFormattedTransactions = List.of(
                        "|------------------------------------ WELCOME TO YOUR BANK ACCOUNT HISTORY ------------------------------------|",
                        "|------------------------------------ CURRENT BALANCE OF USER : 465.00€ ------------------------------------|",
                        "",
                        "|Transaction date      |Transaction type   |Impact on the balance       |Balance after the transaction   ",
                        "|1970-01-01 02:00:00   |DEPOSIT            |10.00€                      |465.00€                     ",
                        "|1970-01-01 02:00:00   |WITHDRAWAL         |-30.00€                     |455.00€                     ",
                        "|1970-01-01 02:00:00   |DEPOSIT            |15.00€                      |485.00€                     ",
                        "|1970-01-01 02:00:00   |DEPOSIT            |42.00€                      |470.00€                     ");

        final List<String> formattedTransactions = table.format(transactions);
        assertEquals(expectedFormattedTransactions, formattedTransactions);
    }

    @Test
    @DisplayName("Should format the history when there is no transaction")
    void shouldFormatHistoryWithNoTransaction() {
        TableTransactionFormatter table = new TableTransactionFormatter();
        List<Transaction> transactions = List.of();

        final List<String> expectedFormattedTransactions = List.of(
                "|------------------------------------ WELCOME TO YOUR BANK ACCOUNT HISTORY ------------------------------------|",
                "|------------------------------------ CURRENT BALANCE OF USER : 0.00€ ------------------------------------|",
                "",
                "|Transaction date      |Transaction type   |Impact on the balance       |Balance after the transaction   ",
                "|No data found         |No data found      |No data found               |No data found               ");

        final List<String> formattedTransactions = table.format(transactions);
        assertEquals(expectedFormattedTransactions, formattedTransactions);
    }

}