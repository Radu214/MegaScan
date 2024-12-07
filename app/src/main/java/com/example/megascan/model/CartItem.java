package com.example.megascan.model;

public class CartItem extends Produs{

    private int quantity;

    public CartItem(String productName, double price, String id, String brand, int quantity) {
        super(productName, price, id, brand);
        this.quantity = quantity;
    }


    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
}
