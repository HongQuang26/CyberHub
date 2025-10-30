package com.yourcompany.cyberhub.model;

import java.math.BigDecimal;

public class MenuItem {
    private int itemId;
    private String name;
    private BigDecimal price;
    private String category;
    private boolean available;
    private int storage;

    public MenuItem(int itemId, String name, BigDecimal price, String category, boolean available, int storage) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.available = available;
        this.storage = storage;
    }

    public int getItemId() { return itemId; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public String getCategory() { return category; }
    public boolean isAvailable() { return available; }
    public int getStorage() { return storage; }

    @Override
    public String toString() {
        return name;
    }
}
