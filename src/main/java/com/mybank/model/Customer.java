package com.mybank.model;

import java.time.LocalDateTime;
import java.util.List;

public class Customer {

    //feilds
    private final String id ;
    private String name;
    private String email;
    private LocalDateTime dateCreated;
    private List<Accounts> accounts;

    //constructor
    public Customer(String id){
        this.id = id;
    }

    //getter setter

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

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }


    //Methods

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
