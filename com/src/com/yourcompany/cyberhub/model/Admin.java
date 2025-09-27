package com.yourcompany.netcafe.model;

public class Admin extends User {
    public Admin(int userId, String username, String password, String fullName) {
        super(userId, username, password, fullName, "ADMIN");
    }
    public Admin() {
        super();
        setRole("ADMIN");
    }
}