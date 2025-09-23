package com.mybank.model;

import java.time.LocalDateTime;

public class Customer {

    //feilds
    private final String id ;
    private String name;
    private String email;
    private LocalDateTime dateCreated;

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
        return "name = "+ name + " , "+ "id" +" , "+ id + " , "+ "email" + email;
    }
}
