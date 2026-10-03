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

    public Account openAccount(String owner) {
        if (owner == null || owner.isBlank()) {
            throw new IllegalArgumentException("Owner name is required");
        }
        Account account = new Account("ACC" + nextNumber++, owner.trim());
        accounts.put(account.getNumber(), account);
        return account;
    }

    public Account find(String number) {
        Account account = accounts.get(number);
        if (account == null) {
            throw new IllegalArgumentException("No account with number " + number);
        }
        return account;
    }

    public void transfer(String from, String to, BigDecimal amount) {
        Account source = find(from);
        Account target = find(to);
        source.withdraw(amount);   // throws first if funds are short, so nothing is lost
        target.deposit(amount);
    }

    public List<Account> allAccounts() {
        return new ArrayList<>(accounts.values());
    }
}
