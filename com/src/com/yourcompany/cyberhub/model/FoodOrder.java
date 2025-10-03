package com.yourcompany.cyberhub.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class FoodOrder {
    private int orderId;
    private int userId;
    private int foodId;
    private int quantity;
    private BigDecimal totalPrice;
    private String status; // PENDING, DELIVERED, CANCELLED
    private Timestamp orderTime;

    public FoodOrder(int orderId, int userId, int foodId, int quantity, BigDecimal totalPrice, String status, Timestamp orderTime) {
        this.orderId = orderId;
        this.userId = userId;
        this.foodId = foodId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status;
        this.orderTime = orderTime;
    }

    public FoodOrder() {
    }

    // Getters and Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getFoodId() { return foodId; }
    public void setFoodId(int foodId) { this.foodId = foodId; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Timestamp getOrderTime() { return orderTime; }
    public void setOrderTime(Timestamp orderTime) { this.orderTime = orderTime; }
}
