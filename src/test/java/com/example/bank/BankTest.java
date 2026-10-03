package com.example.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BankTest {
    private Bank bank;
    private Account alice;
    private Account bob;

    @BeforeEach
    void setUp() {
        bank = new Bank();
        alice = bank.openAccount("Alice");
        bob = bank.openAccount("Bob");
    }

    @Test
    void newAccountStartsAtZero() {
        assertEquals(BigDecimal.ZERO, alice.getBalance());
    }

    @Test
    void depositIncreasesBalance() {
        alice.deposit(new BigDecimal("100.50"));
        assertEquals(new BigDecimal("100.50"), alice.getBalance());
    }

    @Test
    void withdrawDecreasesBalance() {
        alice.deposit(new BigDecimal("100"));
        alice.withdraw(new BigDecimal("40"));
        assertEquals(new BigDecimal("60"), alice.getBalance());
    }

    @Test
    void cannotWithdrawMoreThanBalance() {
        alice.deposit(new BigDecimal("10"));
        assertThrows(InsufficientFundsException.class, () -> alice.withdraw(new BigDecimal("11")));
    }

    @Test
    void rejectsZeroOrNegativeAmounts() {
        assertThrows(IllegalArgumentException.class, () -> alice.deposit(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> alice.deposit(new BigDecimal("-5")));
    }

    @Test
    void transferMovesMoney() {
        alice.deposit(new BigDecimal("100"));
        bank.transfer(alice.getNumber(), bob.getNumber(), new BigDecimal("30"));
        assertEquals(new BigDecimal("70"), alice.getBalance());
        assertEquals(new BigDecimal("30"), bob.getBalance());
    }

    @Test
    void failedTransferLeavesBothBalancesUnchanged() {
        alice.deposit(new BigDecimal("20"));
        assertThrows(InsufficientFundsException.class,
                () -> bank.transfer(alice.getNumber(), bob.getNumber(), new BigDecimal("50")));
        assertEquals(new BigDecimal("20"), alice.getBalance());
        assertEquals(BigDecimal.ZERO, bob.getBalance());
    }

    @Test
    void unknownAccountIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> bank.find("ACC9999"));
    }

    @Test
    void ownerInitialsAreUppercaseFirstLetters() {
        Account account = bank.openAccount("rahul  kumar sharma");
        assertEquals("RKS", account.getOwnerInitials());
        assertEquals("A", alice.getOwnerInitials());
    }

    // ---- New features ----

    @Test
    void cannotTransferToSameAccount() {
        alice.deposit(new BigDecimal("50"));
        assertThrows(IllegalArgumentException.class,
                () -> bank.transfer(alice.getNumber(), alice.getNumber(), new BigDecimal("10")));
    }

    @Test
    void closeAccountWithZeroBalanceRemovesIt() {
        bank.closeAccount(bob.getNumber());
        assertThrows(IllegalArgumentException.class, () -> bank.find(bob.getNumber()));
        assertEquals(1, bank.allAccounts().size());
    }

    @Test
    void cannotCloseAccountThatStillHasMoney() {
        alice.deposit(new BigDecimal("5"));
        assertThrows(IllegalStateException.class, () -> bank.closeAccount(alice.getNumber()));
        assertEquals(2, bank.allAccounts().size());
    }

    @Test
    void findByOwnerIsCaseInsensitive() {
        bank.openAccount("Rahul Kumar");
        List<Account> found = bank.findByOwner("RAH");
        assertEquals(1, found.size());
        assertEquals("Rahul Kumar", found.get(0).getOwner());
        assertTrue(bank.findByOwner("nobody").isEmpty());
    }

    @Test
    void monthlyInterestIsPaidOnlyToSavings() {
        Account current = bank.openAccount("Carol", AccountType.CURRENT);
        alice.deposit(new BigDecimal("1200"));      // savings: 4% a year = 4.00 a month
        current.deposit(new BigDecimal("1200"));    // current: no interest

        BigDecimal paid = bank.applyMonthlyInterest();

        assertEquals(new BigDecimal("4.00"), paid);
        assertEquals(new BigDecimal("1204.00"), alice.getBalance());
        assertEquals(new BigDecimal("1200"), current.getBalance());
    }

    @Test
    void totalBalanceAddsUpAllAccounts() {
        alice.deposit(new BigDecimal("100"));
        bob.deposit(new BigDecimal("50.25"));
        assertEquals(new BigDecimal("150.25"), bank.totalBalance());
    }
}