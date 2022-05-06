package com.example.uscdrinkdoor;

public class orderItem {

    private String productName;
    private long price;
    private String description;
    private long caffeine;

    public orderItem(String p, long pr, String desc){
        this.productName = p;
        this.price = pr;
        this.description = desc;
    }

    public orderItem(String p, long pr, String desc, long caff){
        this.productName = p;
        this.price = pr;
        this.description = desc;
        this.caffeine = caff;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public long getCaffeine() {
        return caffeine;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
