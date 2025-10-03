package com.yourcompany.cyberhub.model;

import java.math.BigDecimal;

public class Customer extends User {
    private BigDecimal balance;

    public Customer(int userId, String username, String password, String fullName, BigDecimal balance) {
        super(userId, username, password, fullName, "CUSTOMER");
        this.balance = balance;
    }

    public Customer() {
        super();
        setRole("CUSTOMER");
    }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}