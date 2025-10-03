package com.yourcompany.cyberhub.model;

import java.math.BigDecimal;

public class MenuItem {
    private int itemId;
    private String name;
    private BigDecimal price;
    private String category;
    private boolean available;

    public MenuItem(int itemId, String name, BigDecimal price, String category, boolean available) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.available = available;
    }

    public int getItemId() { return itemId; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public String getCategory() { return category; }
    public boolean isAvailable() { return available; }

    @Override
    public String toString() {
        return name;
    }
}