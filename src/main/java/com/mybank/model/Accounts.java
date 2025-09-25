package com.mybank.model;

import java.math.BigDecimal;
import java.util.List;

public class Accounts {

    private final String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String accountStatus;
    private Customer owner;

    private List<Transaction>transactions;


    public Accounts(String accountNumber)
    {
        this.accountNumber = accountNumber;
    }

    @Override
    public String toString() {
        return "Accounts{" +
                "accountNumber='" + accountNumber + '\'' +
                ", accountType='" + accountType + '\'' +
                ", balance=" + balance +
                ", accountStatus='" + accountStatus + '\'' +
                ", owner=" + owner +
                ", transactions=" + transactions +
                '}';
    }
}
