package com.v2.mybank.model;

import com.v2.mybank.enums.AccountStatus;
import com.v2.mybank.enums.AccountType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Account {

  private final String accountNumber;
  private AccountType type;
  private double balance;
  private AccountStatus status;
  private Customer owner;
  private final List<Transaction> transactions;

  public Account(String accountNumber) {
    this.accountNumber = accountNumber;
    this.transactions = new ArrayList<>();
  }

  public void deposit(double amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Deposit amount cannot be less than or equal to 0");
    }
    if (this.status != AccountStatus.ACTIVE) {
      throw new IllegalStateException("Cannot deposit to a non-active account");
    }
    this.balance += amount;
  }

  public void withdraw(double amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Withdraw amount cannot be less than or equal to 0");
    }
    if (this.status != AccountStatus.ACTIVE) {
      throw new IllegalStateException("Cannot withdraw from a non-active account");
    }
    double newBalance = this.balance - amount;
    if (newBalance < 0) {
      throw new IllegalArgumentException("Insufficient Balance to withdraw amount " + amount);
    }
    this.balance = newBalance;
  }

  public void addTransaction(Transaction transaction) {
    if (transaction == null) {
      throw new IllegalArgumentException("Cannot add null transaction value");
    }
    this.transactions.add(transaction);
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public AccountType getType() {
    return type;
  }

  public void setType(AccountType type) {
    this.type = type;
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }

  public AccountStatus getStatus() {
    return status;
  }

  public void setStatus(AccountStatus status) {
    this.status = status;
  }

  public Customer getOwner() {
    return owner;
  }

  public void setOwner(Customer owner) {
    this.owner = owner;
  }

  public List<Transaction> getTransactions() {
    return Collections.unmodifiableList(transactions);
  }

  @Override
  public String toString() {
    return "Account{" +
        "accountNumber='" + accountNumber + '\'' +
        ", type=" + type +
        ", balance=" + balance +
        ", status=" + status +
        ", owner=" + owner +
        ", transactions=" + transactions +
        '}';
  }
}
