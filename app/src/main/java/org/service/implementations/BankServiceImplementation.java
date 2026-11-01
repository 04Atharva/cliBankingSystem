package org.service.implementations;

import org.DAO.Account;
import org.DAO.Customer;
import org.DAO.Transaction;
import org.DAO.Type;
import org.exceptions.AccountNotFoundException;
import org.exceptions.InsufficientFundsException;
import org.exceptions.ValidationException;
import org.repository.AccountRepo;
import org.repository.CustomerRepo;
import org.repository.TransactionRepo;
import org.service.BankService;
import org.util.Validation;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class BankServiceImplementation implements BankService {

    private final AccountRepo accountRepo = new AccountRepo();
    private final TransactionRepo transactionRepo = new TransactionRepo();
    private final CustomerRepo customerRepo = new CustomerRepo();

    private final Validation<String> validateName = name -> {
        if (name == null || name.isBlank())
            throw new ValidationException("Name is required");
    };

    private final Validation<String> validateEmail = email -> {
        if (email == null || !email.contains("@"))
            throw new ValidationException("Email is required");
    };

    private final Validation<String> validateType = type -> {
        if (type == null || !(type.equalsIgnoreCase("SAVINGS") || type.contains("CURRENT")))
            throw new ValidationException("Type must be SAVINGS or CURRENT");
    };

    private final Validation<Double> validateAmountPositive = amount -> {
        if (amount == null || amount < 0)
            throw new ValidationException("Please enter valid amount");
    };

    @Override
    public String openAccount(String name, String email, String accountType) {
        validateName.validate(name);
        validateEmail.validate(email);
        validateType.validate(accountType);

        String customerId = UUID.randomUUID().toString();

        Customer c = new Customer(customerId, name, email);
        customerRepo.save(c);

        String accountNumber = getAccountNumber();
        Account account = new Account(accountNumber, customerId, 0.0, accountType);
        accountRepo.save(account);
        return accountNumber;
    }

    @Override
    public List<Account> listAccounts() {
        return accountRepo.findAll().stream()
                .sorted(Comparator.comparing(Account::getAccountNumber))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("")
    @Override
    public void deposit(String accountNumber, Double amount, String note) {
        validateAmountPositive.validate(amount);

        // Explicitly casting the raw Optional to Optional<Account> to clear the
        // compilation error
        Optional<Account> accountOpt = (Optional<Account>) accountRepo.findByNumber(accountNumber);
        Account account = accountOpt
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));

        account.setBalance(account.getBalance() + amount);

        Transaction transaction = new Transaction(UUID.randomUUID().toString(), account.getAccountNumber(),
                amount, note, LocalDateTime.now(), Type.DEPOSIT);
        transactionRepo.add(transaction);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void withdraw(String accountNumber, Double amount, String note) {
        validateAmountPositive.validate(amount);

        // Explicitly casting the raw Optional to Optional<Account> to clear the
        // compilation error
        Optional<Account> accountOpt = (Optional<Account>) accountRepo.findByNumber(accountNumber);
        Account account = accountOpt
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));

        if (account.getBalance() < amount)
            throw new InsufficientFundsException("Insufficient Balance");

        account.setBalance(account.getBalance() - amount);

        Transaction transaction = new Transaction(UUID.randomUUID().toString(), account.getAccountNumber(),
                amount, note, LocalDateTime.now(), Type.WITHDRAW);
        transactionRepo.add(transaction);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void transfer(String fromAcc, String toAcc, Double amount, String transfer) {
        validateAmountPositive.validate(amount);
        if (fromAcc.equals(toAcc))
            throw new ValidationException("Cannot transfer to your own account");

        // Explicitly casting the raw Optionals to clear the compilation errors
        Optional<Account> fromOpt = (Optional<Account>) accountRepo.findByNumber(fromAcc);
        Account from = fromOpt.orElseThrow(() -> new AccountNotFoundException("Account not found: " + fromAcc));

        Optional<Account> toOpt = (Optional<Account>) accountRepo.findByNumber(toAcc);
        Account to = toOpt.orElseThrow(() -> new AccountNotFoundException("Account not found: " + toAcc));

        if (from.getBalance() < amount)
            throw new InsufficientFundsException("Insufficient Balance");

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        transactionRepo.add(new Transaction(UUID.randomUUID().toString(), from.getAccountNumber(),
                amount, transfer, LocalDateTime.now(), Type.TRANSFER_OUT));

        transactionRepo.add(new Transaction(UUID.randomUUID().toString(), to.getAccountNumber(),
                amount, transfer, LocalDateTime.now(), Type.TRANSFER_IN));
    }

    @Override
    public List<Transaction> getStatement(String account) {
        return transactionRepo.findByAccount(account).stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp))
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> searchAccountByCustomerName(String q) {
        String query = (q == null) ? "" : q.toLowerCase();

        return customerRepo.findAll().stream()
                .filter(c -> c.getName().toLowerCase().contains(query))
                .flatMap(c -> accountRepo.findByCustomerId(c.getId()).stream())
                .sorted(Comparator.comparing(Account::getAccountNumber))
                .collect(Collectors.toList());
    }

    private String getAccountNumber() {
        int size = accountRepo.findAll().size() + 1;
        return String.format("AC%06d", size);
    }
}
