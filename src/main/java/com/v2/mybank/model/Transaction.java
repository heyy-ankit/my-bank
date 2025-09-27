package com.v2.mybank.model;

import com.v2.mybank.enums.TransactionType;

import java.time.LocalDateTime;

public final class Transaction {

  private final String id;
  private final LocalDateTime timestamp;
  private final TransactionType type;
  private final double amount;
  private final double balanceAfter;
  private final String description;

  public Transaction(String id, TransactionType type, double amount, double balanceAfter, String description) {
    this.id = id;
    this.timestamp = LocalDateTime.now();
    this.type = type;
    this.amount = amount;
    this.balanceAfter = balanceAfter;
    this.description = description;
  }

  public String getId() {
    return id;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public TransactionType getType() {
    return type;
  }

  public double getAmount() {
    return amount;
  }

  public double getBalanceAfter() {
    return balanceAfter;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return "Transaction{" +
        "id='" + id + '\'' +
        ", timestamp=" + timestamp +
        ", type=" + type +
        ", amount=" + amount +
        ", balanceAfter=" + balanceAfter +
        ", description='" + description + '\'' +
        '}';
  }
}
