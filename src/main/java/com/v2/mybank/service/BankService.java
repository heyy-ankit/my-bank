package com.v2.mybank.service;

import com.v2.mybank.model.Account;
import com.v2.mybank.model.Customer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BankService {

  private final Map<String, Customer> customersById;
  private final Map<String, Account> accountsByNumber;

  public BankService() {
    this.customersById = new HashMap<>();
    this.accountsByNumber = new HashMap<>();
  }

  public Customer createCustomer(String name, String email) {
    // Add name validations
    // Add email validations
    String customerId = "C-"+generateRandomId();
    Customer customer = new Customer(customerId);
    customer.setName(name);
    customer.setEmail(email);
    customersById.put(customerId, customer);

    return customer;
  }

  public String listCustomers() {
    StringBuilder sb = new StringBuilder("[\n");
    int i = 1;
    for (Customer customer : customersById.values()) {
      sb.append("  ").append(i++).append(". ").append(customer).append("\n");
    }
    sb.append("]");
    return sb.toString();
  }

  private String generateRandomId() {
    String uuid = UUID.randomUUID().toString();
    String[] parts = uuid.split("-");
    return parts[0].toUpperCase();
  }

}
