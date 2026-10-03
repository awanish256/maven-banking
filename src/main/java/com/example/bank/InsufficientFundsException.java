package com.example.bank;

/** Thrown when a withdrawal or transfer asks for more money than the account holds. */
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
