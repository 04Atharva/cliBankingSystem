
package org.service;

import org.DAO.Account;
import org.DAO.Transaction;

import java.util.List;

public interface BankService {

    String openAccount(String name, String email, String accountType);

    void deposit(String accountNumber, Double amount, String note);
    void withdraw(String accountNumber, Double amount, String withdrawl);
    void transfer(String from, String to, Double amount, String transfer);

    List<Account> listAccounts();
    List<Account> searchAccountByCustomerName(String q); 
    List<Transaction> getStatement(String account);
}
