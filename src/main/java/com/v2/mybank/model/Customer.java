package com.v2.mybank.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Customer {

  private final String id;
  private String name;
  private String email;
  private final LocalDateTime dateCreated;
  private final List<Account> accounts;

  public Customer(String id) {
    this.id = id;
    this.accounts = new ArrayList<>();
    this.dateCreated = LocalDateTime.now();
  }

  public void addAccount(Account account) {
    if (account == null) {
      throw new IllegalArgumentException("Account cannot be null");
    }
    if (account.getOwner() != this) {
      throw new IllegalStateException("This account does not belong to current customer");
    }
    this.accounts.add(account);
  }

  public Optional<Account> findAccountByNumber(String accountNumber) {
    if (accountNumber == null || accountNumber.isBlank()) {
      throw new IllegalArgumentException("Account number cannot be empty: " + accountNumber);
    }
    for (int i = 0; i < this.accounts.size(); i++) {
      Account account = this.accounts.get(0);
      if (account.getAccountNumber().equals(accountNumber)) {
        return Optional.of(account);
      }
    }
    return Optional.empty();
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public LocalDateTime getDateCreated() {
    return dateCreated;
  }

  public List<Account> getAccounts() {
    return Collections.unmodifiableList(accounts);
  }

  @Override
  public String toString() {
    return "Customer{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", email='" + email + '\'' +
        ", dateCreated=" + dateCreated +
        ", accounts=" + accounts +
        '}';
  }

}
