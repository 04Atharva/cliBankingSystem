
package org.repository;

import org.DAO.Transaction;

import java.util.*;

public class TransactionRepo {

    private final Map<String, List<Transaction>> txByAccount = new HashMap<>();

    public void add(Transaction transaction) {

        List<Transaction> list = txByAccount.computeIfAbsent(transaction.getAccountNumber(), k -> new ArrayList<>());
        list.add(transaction);
    }

    public List<Transaction> findByAccount(String account) {
        return new ArrayList<>(txByAccount.getOrDefault(account, Collections.emptyList()));
    }
}
