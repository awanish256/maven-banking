package com.example.bank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A single bank account. BigDecimal is used because double cannot store money exactly. */
public class Account {
    private static final BigDecimal MONTHS_PER_YEAR = new BigDecimal("12");

    private final String number;
    private final String owner;
    private final AccountType type;
    private BigDecimal balance = BigDecimal.ZERO;
    private final List<Transaction> transactions = new ArrayList<>();

    public Account(String number, String owner, AccountType type) {
        this.number = number;
        this.owner = owner;
        this.type = type;
    }

    public Account(String number, String owner) {
        this(number, owner, AccountType.SAVINGS);
    }

    public void deposit(BigDecimal amount) {
        requirePositive(amount);
        credit(amount, TransactionType.DEPOSIT, "");
    }

    public void withdraw(BigDecimal amount) {
        requirePositive(amount);
        requireAvailable(amount);
        debit(amount, TransactionType.WITHDRAWAL, "");
    }

    /** Used by Bank.transfer so both sides show up on statements. */
    void transferOut(BigDecimal amount, String toAccount) {
        requirePositive(amount);
        requireAvailable(amount);
        debit(amount, TransactionType.TRANSFER_OUT, "to " + toAccount);
    }

    void transferIn(BigDecimal amount, String fromAccount) {
        requirePositive(amount);
        credit(amount, TransactionType.TRANSFER_IN, "from " + fromAccount);
    }

    /**
     * Adds one month of interest (annual rate / 12), rounded to 2 decimals.
     * Only SAVINGS accounts with a positive balance earn interest.
     * Returns the interest added (zero if none).
     */
    public BigDecimal applyMonthlyInterest() {
        if (balance.signum() <= 0 || type.getAnnualInterestRate().signum() == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal interest = balance.multiply(type.getAnnualInterestRate())
                .divide(MONTHS_PER_YEAR, 2, RoundingMode.HALF_EVEN);
        if (interest.signum() > 0) {
            credit(interest, TransactionType.INTEREST, "monthly interest");
        }
        return interest;
    }

    /** How much can be withdrawn right now, including any overdraft. */
    public BigDecimal getAvailableFunds() {
        return balance.add(type.getOverdraftLimit());
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

    private void credit(BigDecimal amount, TransactionType kind, String note) {
        balance = balance.add(amount);
        transactions.add(new Transaction(LocalDateTime.now(), kind, amount, balance, note));
    }

    private void debit(BigDecimal amount, TransactionType kind, String note) {
        balance = balance.subtract(amount);
        transactions.add(new Transaction(LocalDateTime.now(), kind, amount, balance, note));
    }

    private void requireAvailable(BigDecimal amount) {
        if (getAvailableFunds().compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                    "Account " + number + " has only " + getAvailableFunds()
                            + " available, cannot withdraw " + amount);
        }
    }

    private static void requirePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    public String getNumber() { return number; }
    public String getOwner() { return owner; }
    public AccountType getType() { return type; }
    public BigDecimal getBalance() { return balance; }

    /** Read-only view, so callers cannot add fake transactions. */
    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    @Override
    public String toString() {
        return number + " | " + owner + " | " + type + " | balance " + balance;
    }
}