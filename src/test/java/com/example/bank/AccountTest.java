package com.example.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class AccountTest {

    @Test
    void currentAccountCanUseOverdraft() {
        Account current = new Account("ACC1", "Dev", AccountType.CURRENT);
        current.deposit(new BigDecimal("100"));
        current.withdraw(new BigDecimal("600"));
        assertEquals(new BigDecimal("-500"), current.getBalance());
        assertEquals(new BigDecimal("500"), current.getAvailableFunds());
    }

    @Test
    void currentAccountCannotGoPastOverdraftLimit() {
        Account current = new Account("ACC1", "Dev", AccountType.CURRENT);
        assertThrows(InsufficientFundsException.class,
                () -> current.withdraw(new BigDecimal("1000.01")));
        assertEquals(BigDecimal.ZERO, current.getBalance());
    }

    @Test
    void savingsAccountHasNoOverdraft() {
        Account savings = new Account("ACC1", "Sam", AccountType.SAVINGS);
        assertThrows(InsufficientFundsException.class, () -> savings.withdraw(new BigDecimal("1")));
    }

    @Test
    void everyOperationIsRecordedOnTheStatement() {
        Account account = new Account("ACC1", "Sam");
        account.deposit(new BigDecimal("100"));
        account.withdraw(new BigDecimal("30"));

        List<Transaction> history = account.getTransactions();

        assertEquals(2, history.size());
        assertEquals(TransactionType.DEPOSIT, history.get(0).type());
        assertEquals(new BigDecimal("100"), history.get(0).balanceAfter());
        assertEquals(TransactionType.WITHDRAWAL, history.get(1).type());
        assertEquals(new BigDecimal("70"), history.get(1).balanceAfter());
    }

    @Test
    void failedWithdrawalIsNotRecorded() {
        Account account = new Account("ACC1", "Sam");
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(new BigDecimal("10")));
        assertEquals(0, account.getTransactions().size());
    }

    @Test
    void transferShowsOnBothStatements() {
        Bank bank = new Bank();
        Account from = bank.openAccount("Asha");
        Account to = bank.openAccount("Ben");
        from.deposit(new BigDecimal("80"));

        bank.transfer(from.getNumber(), to.getNumber(), new BigDecimal("25"));

        Transaction out = from.getTransactions().get(1);
        Transaction in = to.getTransactions().get(0);
        assertEquals(TransactionType.TRANSFER_OUT, out.type());
        assertEquals("to " + to.getNumber(), out.note());
        assertEquals(TransactionType.TRANSFER_IN, in.type());
        assertEquals("from " + from.getNumber(), in.note());
    }

    @Test
    void statementCannotBeModifiedFromOutside() {
        Account account = new Account("ACC1", "Sam");
        assertThrows(UnsupportedOperationException.class,
                () -> account.getTransactions().clear());
    }

    @Test
    void interestIsRoundedToTwoDecimals() {
        Account savings = new Account("ACC1", "Sam");
        savings.deposit(new BigDecimal("1000"));          // 1000 * 0.04 / 12 = 3.333...
        assertEquals(new BigDecimal("3.33"), savings.applyMonthlyInterest());
        assertEquals(new BigDecimal("1003.33"), savings.getBalance());
    }

    @Test
    void noInterestOnEmptyAccount() {
        Account savings = new Account("ACC1", "Sam");
        assertEquals(BigDecimal.ZERO, savings.applyMonthlyInterest());
        assertEquals(0, savings.getTransactions().size());
    }
}