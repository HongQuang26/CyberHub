package com.yourcompany.cyberhub.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {
    private int orderId;
    private int userId;
    private int sessionId;
    private LocalDateTime orderTime;
    private BigDecimal totalAmount;
    private boolean isPaid;

    public Order(int userId, int sessionId, BigDecimal totalAmount) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.totalAmount = totalAmount;
        this.orderTime = LocalDateTime.now();
        this.isPaid = true;
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getUserId() { return userId; }
    public int getSessionId() { return sessionId; }
    public LocalDateTime getOrderTime() { return orderTime; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public boolean isPaid() { return isPaid; }
}