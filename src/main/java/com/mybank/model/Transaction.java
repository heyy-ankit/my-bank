package com.mybank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private final String id;
    private LocalDateTime timestamp;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String description;

    public Transaction(String id)
    {
        this.id = id;

    }



    //getter

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", transactionType='" + transactionType + '\'' +
                ", amount=" + amount +
                ", balanceAfter=" + balanceAfter +
                ", description='" + description + '\'' +
                '}';
    }


}
