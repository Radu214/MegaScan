package com.example.megascan.model;

public class PromoItem extends Produs{
    private String imageUrl;
    private double discountedPrice;

    public PromoItem(String code, double originalPrice, String name, String brand, int plus18, String imageUrl , double discountedPrice) {
        super(code, originalPrice, name, brand, plus18);
        this.imageUrl = imageUrl;
        this.discountedPrice = discountedPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }
}
