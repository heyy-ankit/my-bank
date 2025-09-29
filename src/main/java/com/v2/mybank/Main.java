package com.v2.mybank;

import com.v2.mybank.enums.AccountType;
import com.v2.mybank.model.Account;
import com.v2.mybank.model.Customer;
import com.v2.mybank.service.BankService;

import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {

  private static final Scanner scanner = new Scanner(new InputStreamReader(System.in));
  private static final BankService bankService = new BankService();

  public static void main(String[] args) {
    run();
  }

  private static void run() {
    boolean stop = false;
    System.out.println("""
        ========= BANK MENU =========
        0. Exit
        1. Create Customer
        2. Open Account
        3. List Customer Accounts
        4. Deposit
        5. Withdraw
        6. Transfer Between Accounts
        7. View Account Balance
        8. View Transaction History
        9. View All Customers""");
    while (!stop) {
      showMainMenu();
      int option = readInt(scanner.next());
      stop = handleUserSelection(option);
    }
  }

  private static void showMainMenu() {
    System.out.print("\nSelect option: ");
  }

  private static boolean handleUserSelection(int choice) {
    if (choice == -1) {
      System.out.println("Please enter correct option no.");
      return false;
    }
    if (choice < 0 || choice > 9) {
      System.out.println("Please choose an option in between 0 to 9");
      return false;
    }
    if (choice == 0) {
      System.out.println("GoodBye! 👋");
      return true;
    }
    if (choice == 1) {
      System.out.print("Enter name: ");
      String name = scanner.next();
      System.out.print("Enter email: ");
      String email = scanner.next();
      Customer customer = bankService.createCustomer(name, email);
      System.out.println("Customer created with ID: " + customer.getId());
    }
    if (choice == 2) {
      System.out.print("Enter customer ID: ");
      String customerId = scanner.next();
      System.out.print("Select account type (CHECKING, SAVINGS): ");
      String accountType = scanner.next();
      Account account = bankService.openAccount(customerId, AccountType.valueOf(accountType));
      System.out.println("Account created with ID: " + account.getAccountNumber());
    }
    if (choice == 3) {
      System.out.println(bankService.listCustomerAccounts());
    }
    if (choice == 9) {
      System.out.println(bankService.listCustomers());
    }
    return false;
  }

  private static int readInt(String prompt) {
    try {
      return Integer.parseInt(prompt);
    } catch (NumberFormatException ignored) {}
    return -1;
  }

}
