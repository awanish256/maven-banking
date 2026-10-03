package com.example.bank;

import java.math.BigDecimal;
import java.util.Scanner;

/** Console menu. This is the class "java -jar" starts (see Main-Class in pom.xml). */
public class BankApp {

    public static void main(String[] args) {
        Bank bank = new Bank();
        Scanner in = new Scanner(System.in);
        System.out.println("=== Simple Bank ===");

        while (true) {
            System.out.println();
            System.out.println("1) Open account   2) Deposit   3) Withdraw");
            System.out.println("4) Transfer       5) Balance   6) List accounts   0) Exit");
            System.out.print("Choose: ");
            if (!in.hasNextLine()) {
                return;
            }
            String choice = in.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> {
                        Account a = bank.openAccount(ask(in, "Owner name: "));
                        System.out.println("Opened " + a.getNumber() + " for " + a.getOwner());
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
                    }
                    case "6" -> {
                        if (bank.allAccounts().isEmpty()) {
                            System.out.println("No accounts yet.");
                        }
                        bank.allAccounts().forEach(System.out::println);
                    }
                    case "0" -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Unknown option.");
                }
            } catch (IllegalArgumentException | InsufficientFundsException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
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
