
package org.DAO;

import java.time.LocalDateTime;

import java.time.LocalDateTime;

public class Transaction {

    private String id;
    private String accountNumber;
    private double amount;
    private LocalDateTime timestamp;
    private String note;
    private Type type;

    public Transaction(String id, String accountNumber, double amount, String note, LocalDateTime timestamp,
            Type type) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.note = note;
        this.timestamp = timestamp;
        this.type = type;
    }

    // getters and getters
    public String getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Type getType() {
        return type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setType(Type type) {
        this.type = type;
    }

}
