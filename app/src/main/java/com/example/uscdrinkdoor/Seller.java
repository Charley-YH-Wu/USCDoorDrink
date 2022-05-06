package com.example.uscdrinkdoor;

import android.location.Location;

import java.util.List;

public class Seller extends User{
    //com.example.uscdrinkdoor.User data
    int userID;

    //Store Information
    String storeName;
    String storeDescription;
    Location location;

    //Store components
    List<Item> menu;
    List<Order> currentOrders;
    List<Order> pastOrders;

    public Seller(String storeName, String storeDescription, Location location){

        this.storeName = storeName;
        this.storeDescription = storeDescription;
        this.location = location;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreDescription() {
        return storeDescription;
    }

    public void setStoreDescription(String storeDescription) {
        this.storeDescription = storeDescription;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void AddOrder(Order order){
        this.currentOrders.add(order);
    }

    public void sendOrder(){
       //implemented on activity

        //get first order from current orders
        //send to API
        //remove from current orders
        //add to pastOrders
    }

    public void AddItem(Item item){
        this.menu.add(item);
    }

    public void removeItem(Item item){
        // remove item
    }




}
