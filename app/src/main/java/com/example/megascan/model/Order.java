package com.example.megascan.model;

import java.util.List;

public class Order {
    private int orderId;
    private String createdAt;
    private List<OrderItem> items;

    public int getOrderId() {
        return orderId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}
