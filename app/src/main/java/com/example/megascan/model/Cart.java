package com.example.megascan.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private final static Cart instance = new Cart();

    private Cart() {
        plus18 = 0;
        employeeChecked = false;
    }

    List<CartItem> itemList = new ArrayList<CartItem>();
    Integer total;
    int plus18;
    boolean employeeChecked;

    public boolean isEmployeeChecked() {
        return employeeChecked;
    }

    public void setEmployeeChecked(boolean employeeChecked) {
        this.employeeChecked = employeeChecked;
    }

    public int getPlus18() {
        return plus18;
    }

    public void setPlus18(int plus18) {
        this.plus18 = plus18;
    }

    public static Cart getInstance() {
        return instance;
    }

    public List<CartItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<CartItem> itemList) {
        this.itemList = itemList;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public void addToCart(Produs produs) {
        if (produs.getPLUS18() == 1 && !employeeChecked) {
            plus18++;
        }
        // Check if the item already exists in the cart
        for (CartItem item : itemList) {
            if (item.getCod().equals(produs.getCod())) {
                // If found, increase the quantity by 1
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }

        // If the item doesn't exist, add it to the cart
        CartItem newItem = new CartItem(produs.getDenumire(), produs.getPret(), produs.getCod(), produs.getFirma(), produs.getPLUS18(), 1);
        itemList.add(newItem);
    }

    public void clearCart() {

        itemList.clear();


        total = 0;
        plus18 = 0;


        employeeChecked = false;
    }
}
