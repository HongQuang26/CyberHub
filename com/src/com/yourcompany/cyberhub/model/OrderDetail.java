package com.yourcompany.cyberhub.model;

import java.math.BigDecimal;

public class OrderDetail {
    private int orderDetailId;
    private int orderId;
    private int itemId;
    private int quantity;
    private BigDecimal pricePerItem;

    public OrderDetail(int itemId, int quantity, BigDecimal pricePerItem) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
    }

    public int getItemId() { return itemId; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPricePerItem() { return pricePerItem; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
}