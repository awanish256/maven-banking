package com.example.bank;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/** Console menu. This is the class "java -jar" starts (see Main-Class in pom.xml). */
public class BankApp {

    public static void main(String[] args) {
        Bank bank = new Bank();
        Scanner in = new Scanner(System.in);
        System.out.println("=== Simple Bank ===");

        while (true) {
            System.out.println();
            System.out.println(" 1) Open account    2) Deposit          3) Withdraw");
            System.out.println(" 4) Transfer        5) Balance          6) List accounts");
            System.out.println(" 7) Statement       8) Monthly interest 9) Close account");
            System.out.println("10) Search by name 11) Bank summary     0) Exit");
            System.out.print("Choose: ");
            if (!in.hasNextLine()) {
                return;
            }
            String choice = in.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> {
                        String owner = ask(in, "Owner name: ");
                        AccountType type = accountType(in);
                        Account a = bank.openAccount(owner, type);
                        System.out.println("Opened " + type + " account " + a.getNumber() + " for " + a.getOwner());
                    }
                    case "2" -> {
                        Account a = bank.find(ask(in, "Account number: "));
                        a.deposit(amount(in));
                        System.out.println("New balance: " + a.getBalance());
                    }
                    case "3" -> {
                        Account a = bank.find(ask(in, "Account number: "));
                        a.withdraw(amount(in));
                        System.out.println("New balance: " + a.getBalance());
                    }
                    case "4" -> {
                        String from = ask(in, "From account: ");
                        String to = ask(in, "To account: ");
                        bank.transfer(from, to, amount(in));
                        System.out.println("Transfer done.");
                    }
                    case "5" -> {
                        Account a = bank.find(ask(in, "Account number: "));
                        System.out.println(a);
                        System.out.println("Available to withdraw: " + a.getAvailableFunds());
                    }
                    case "6" -> printAccounts(bank.allAccounts());
                    case "7" -> {
                        Account a = bank.find(ask(in, "Account number: "));
                        System.out.println("Statement for " + a);
                        if (a.getTransactions().isEmpty()) {
                            System.out.println("No transactions yet.");
                        }
                        a.getTransactions().forEach(System.out::println);
                    }
                    case "8" -> System.out.println("Interest paid: " + bank.applyMonthlyInterest());
                    case "9" -> {
                        String number = ask(in, "Account number: ");
                        bank.closeAccount(number);
                        System.out.println("Closed " + number);
                    }
                    case "10" -> printAccounts(bank.findByOwner(ask(in, "Name contains: ")));
                    case "11" -> {
                        System.out.println("Accounts: " + bank.allAccounts().size());
                        System.out.println("Total balance: " + bank.totalBalance());
                    }
                    case "0" -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Unknown option.");
                }
            } catch (IllegalArgumentException | IllegalStateException | InsufficientFundsException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void printAccounts(List<Account> accounts) {
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
        }
        accounts.forEach(System.out::println);
    }

    private static AccountType accountType(Scanner in) {
        String text = ask(in, "Type (S = savings, C = current) [S]: ").toUpperCase();
        return switch (text) {
            case "", "S" -> AccountType.SAVINGS;
            case "C" -> AccountType.CURRENT;
            default -> throw new IllegalArgumentException("'" + text + "' is not S or C");
        };
    }

    private static String ask(Scanner in, String prompt) {
        System.out.print(prompt);
        return in.hasNextLine() ? in.nextLine().trim() : "";
    }

    private static BigDecimal amount(Scanner in) {
        String text = ask(in, "Amount: ");
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + text + "' is not a valid amount");
        }
    }
}