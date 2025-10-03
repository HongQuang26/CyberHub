package com.yourcompany.cyberhub.model;

import java.math.BigDecimal;

public class FoodItem {
    private int foodId;
    private String foodName;
    private BigDecimal price;
    private String category;
    private boolean available;

    public FoodItem(int foodId, String foodName, BigDecimal price, String category, boolean available) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.price = price;
        this.category = category;
        this.available = available;
    }

    public FoodItem() {
    }

    // Getters and Setters
    public int getFoodId() { return foodId; }
    public void setFoodId(int foodId) { this.foodId = foodId; }
    
    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
