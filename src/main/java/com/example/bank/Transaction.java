package com.example.bank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** One line on an account statement. A record is a short way to write an immutable data class. */
public record Transaction(LocalDateTime time, TransactionType type, BigDecimal amount,
                          BigDecimal balanceAfter, String note) {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public String toString() {
        return String.format("%s  %-12s %10s  balance %10s  %s",
                time.format(FORMAT), type, amount, balanceAfter, note);
    }
}