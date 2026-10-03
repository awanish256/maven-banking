package com.example.bank;

import java.math.BigDecimal;

/** A single bank account. BigDecimal is used because double cannot store money exactly. */
public class Account {
    private final String number;
    private final String owner;
    private BigDecimal balance = BigDecimal.ZERO;

    public Account(String number, String owner) {
        this.number = number;
        this.owner = owner;
    }

    public void deposit(BigDecimal amount) {
        requirePositive(amount);
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        requirePositive(amount);
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                    "Account " + number + " has only " + balance + ", cannot withdraw " + amount);
        }
        balance = balance.subtract(amount);
    }

    private static void requirePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }
    /** "Rahul Kumar" -> "RK", "alice" -> "A". */
    public String getOwnerInitials() {
        StringBuilder initials = new StringBuilder();
        for (String word : owner.trim().split("\\s+")) {
            if (!word.isEmpty()) {
                initials.append(Character.toUpperCase(word.charAt(0)));
            }
        }
        return initials.toString();
    }

    public String getNumber() { return number; }
    public String getOwner() { return owner; }
    public BigDecimal getBalance() { return balance; }

    @Override
    public String toString() {
        return number + " | " + owner + " | balance " + balance;
    }
}
