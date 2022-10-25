package utils;

import transaction.Transaction;

import java.util.List;

public interface TransactionFormatter {
    List<String> format(List<Transaction> transactions);
}
