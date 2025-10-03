package com.yourcompany.cyberhub.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private int transactionId;
    private int userId;
    private String username; // Thêm để dễ hiển thị
    private BigDecimal amount;
    private String transactionType; // TOP_UP, PAYMENT
    private LocalDateTime transactionDate;

    public Transaction(int transactionId, int userId, String username, BigDecimal amount, String transactionType, LocalDateTime transactionDate) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.username = username;
        this.amount = amount;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
    }

    // Getters and Setters
    // ... (Thêm các getter setter cần thiết)
    public String getUsername() { return username; }
    public BigDecimal getAmount() { return amount; }
    public String getTransactionType() { return transactionType; }
    public LocalDateTime getTransactionDate() { return transactionDate; }
}