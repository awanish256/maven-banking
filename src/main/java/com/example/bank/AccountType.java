package com.example.bank;

import java.math.BigDecimal;

/**
 * SAVINGS earns monthly interest but cannot go below zero.
 * CURRENT earns no interest but may go overdrawn up to its overdraft limit.
 */
public enum AccountType {
    SAVINGS(new BigDecimal("0.04"), BigDecimal.ZERO),
    CURRENT(BigDecimal.ZERO, new BigDecimal("1000"));

    private final BigDecimal annualInterestRate;
    private final BigDecimal overdraftLimit;

    AccountType(BigDecimal annualInterestRate, BigDecimal overdraftLimit) {
        this.annualInterestRate = annualInterestRate;
        this.overdraftLimit = overdraftLimit;
    }

    public BigDecimal getAnnualInterestRate() { return annualInterestRate; }
    public BigDecimal getOverdraftLimit() { return overdraftLimit; }
}