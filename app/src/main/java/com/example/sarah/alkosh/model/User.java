package com.example.sarah.alkosh.model;

/**
 * Created by Sarah on 11/30/2017.
 */

public class User {

    private String Name;
    private String Phone;

    public User(String name, String phone) {
        Name = name;
        Phone = phone;
    }

    public User() {}

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
