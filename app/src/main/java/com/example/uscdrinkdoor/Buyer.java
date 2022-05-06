package com.example.uscdrinkdoor;

import java.util.*;


public class Buyer extends User{

    //user data
    int userID;

    //user information
    Location location;
    String name;
    String birthday;

    //user components
    Order currentOrder;
    List<Order> pastOrders;
    int dailyCaffeine;


    public Buyer(String name, String birthday, Location location) {
        this.name = name;
        this.birthday = birthday;
        this.location = location;
    }

    //getters
    public String getName() { return name;}

    public String getBirthday() { return birthday;}

    public Location getLocation() { return location;}

    //setters
    public void setName(String name) {this.name = name;}

    public void setBirthday(String birthday) {this.birthday = birthday;}

    public void setLocation(Location location) {this.location = location;}

    //add current order
    public void addOrder(Order order) {this.currentOrder = order;}

    //add to past orders
    public void addPastOrder(Order order) {this.pastOrders.add(order);}



}