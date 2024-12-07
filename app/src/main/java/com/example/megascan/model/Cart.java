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
    public void addToCart(Produs produs){
        CartItem item = new CartItem(produs.getDenumire(), produs.getPret(), produs.getCod(), produs.getFirma(), 1);
        itemList.add(item);
    }
}
