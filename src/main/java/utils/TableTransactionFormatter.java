package utils;

import transaction.Transaction;
import transaction.TypeTransaction;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TableTransactionFormatter implements TransactionFormatter {

    private static final String DATE_FORMATTER = "yyyy-MM-dd HH:mm:ss";
    private static final int maxCharactersInTransactionDate = 23;
    private static final int maxCharactersInTransactionType = 20;
    private static final int maxCharactersInAmountOfTransaction = 29;
    private static final int maxCharactersInBalanceAfterTransaction = 33;
    private static final int maxCharactersInRow = maxCharactersInTransactionDate + maxCharactersInTransactionType + maxCharactersInAmountOfTransaction;

    @Override
    public List<String> format(List<Transaction> transactions) {
        List<String> header = createHeader(transactions);
        List<String> body = createBodyWithTransactionsFormatted(transactions);
        return Stream.concat(header.stream(), body.stream())
                .collect(Collectors.toList());
    }

    private static List<String> createHeader(List<Transaction> transactions){
        var balance = "";
        if(transactions.size() == 0) balance = "0.00€ ";
        else balance = transactions.get(0).balanceAfterExecution().amountValueToString() + "€ ";

        return List.of("|" + returnCorrectNumberOfHyphens() + " WELCOME TO YOUR BANK ACCOUNT HISTORY " + returnCorrectNumberOfHyphens() + "|",
                "|" + returnCorrectNumberOfHyphens() + " CURRENT BALANCE OF USER : " + balance + returnCorrectNumberOfHyphens() + "|",
                "",
                padString("|Transaction date", maxCharactersInTransactionDate)
                + padString("|Transaction type", maxCharactersInTransactionType)
                + padString("|Impact on the balance", maxCharactersInAmountOfTransaction)
                + padString("|Balance after the transaction", maxCharactersInBalanceAfterTransaction)
                );
    }

    private static List<String> createBodyWithTransactionsFormatted(List<Transaction> transactions){
        List<String> transactionsFormatted = new ArrayList<>();
        if(transactions.size() == 0) transactionsFormatted.add(
                padString("|No data found", maxCharactersInTransactionDate)
                + padString("|No data found", maxCharactersInTransactionType)
                + padString("|No data found", maxCharactersInAmountOfTransaction)
                + padString("|No data found", maxCharactersInAmountOfTransaction));
        else{
            for(Transaction transaction: transactions){
                transactionsFormatted.add(
                        padString("|" + transaction.date().format(DateTimeFormatter.ofPattern(DATE_FORMATTER)), maxCharactersInTransactionDate)
                                + padString("|"  +transaction.type().toString(), maxCharactersInTransactionType)
                                + padString("|"  +getImpactOnBalance(transaction) + "€", maxCharactersInAmountOfTransaction)
                                + padString("|"  +transaction.balanceAfterExecution().amountValueToString()+ "€", maxCharactersInAmountOfTransaction)
                );
            }
        }
        return transactionsFormatted;
    }

    private static String returnCorrectNumberOfHyphens(){
        return ("-".repeat(maxCharactersInRow/2));
    }

    private static String padString(String string, int numberOfChars){
        return String.format("%1$-" + numberOfChars + "s", string);
    }

    private static String getImpactOnBalance(Transaction transaction){
        if(transaction.type() == TypeTransaction.DEPOSIT) return transaction.amount().amountValueToString();
        else return "-" + transaction.amount().amountValueToString();
    }
}
