package com.example.bank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Keeps all accounts in memory and performs operations on them. */
public class Bank {
    private final Map<String, Account> accounts = new LinkedHashMap<>();
    private int nextNumber = 1001;

    public Account openAccount(String owner, AccountType type) {
        if (owner == null || owner.isBlank()) {
            throw new IllegalArgumentException("Owner name is required");
        }
        Account account = new Account("ACC" + nextNumber++, owner.trim(), type);
        accounts.put(account.getNumber(), account);
        return account;
    }

    /** Opens a SAVINGS account. */
    public Account openAccount(String owner) {
        return openAccount(owner, AccountType.SAVINGS);
    }

    public Account find(String number) {
        Account account = accounts.get(number);
        if (account == null) {
            throw new IllegalArgumentException("No account with number " + number);
        }
        return account;
    }

    public void transfer(String from, String to, BigDecimal amount) {
        if (from.equals(to)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        Account source = find(from);
        Account target = find(to);
        source.transferOut(amount, to);   // throws first if funds are short, so nothing is lost
        target.transferIn(amount, from);
    }

    /** Closes an account. Its balance must be exactly zero (withdraw or transfer the rest first). */
    public void closeAccount(String number) {
        Account account = find(number);
        if (account.getBalance().signum() != 0) {
            throw new IllegalStateException(
                    "Account " + number + " still has balance " + account.getBalance()
                            + "; bring it to zero before closing");
        }
        accounts.remove(number);
    }

    /** Case-insensitive search on the owner's name, e.g. "rah" finds "Rahul Kumar". */
    public List<Account> findByOwner(String text) {
        String needle = text.trim().toLowerCase();
        List<Account> matches = new ArrayList<>();
        for (Account account : accounts.values()) {
            if (account.getOwner().toLowerCase().contains(needle)) {
                matches.add(account);
            }
        }
        return matches;
    }

    /** Applies one month of interest to every account; returns the total interest paid. */
    public BigDecimal applyMonthlyInterest() {
        BigDecimal total = BigDecimal.ZERO;
        for (Account account : accounts.values()) {
            total = total.add(account.applyMonthlyInterest());
        }
        return total;
    }

    /** Sum of all balances (overdrawn accounts count as negative). */
    public BigDecimal totalBalance() {
        BigDecimal total = BigDecimal.ZERO;
        for (Account account : accounts.values()) {
            total = total.add(account.getBalance());
        }
        return total;
    }

    public List<Account> allAccounts() {
        return new ArrayList<>(accounts.values());
    }
}