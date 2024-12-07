package com.example.megascan.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private final static Cart instance = new Cart();

    private Cart(){};
    List<CartItem> itemList = new ArrayList<CartItem>();
    Integer total;


    public static Cart getInstance() {
        return instance;
    }

    public List<CartItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<CartItem> produsList) {
        this.itemList = itemList;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
    public void addToCart(Produs produs) {
        // Check if the item already exists in the cart
        for (CartItem item : itemList) {
            if (item.getCod().equals(produs.getCod())) {
                // If found, increase the quantity by 1
                item.setQuantity(item.getQuantity() + 1);
                return; // Exit the method as we don't need to add a new item
            }
        }

        // If the item doesn't exist, add it to the cart
        CartItem newItem = new CartItem(produs.getDenumire(), produs.getPret(), produs.getCod(), produs.getFirma(), 1);
        itemList.add(newItem);
    }

}
