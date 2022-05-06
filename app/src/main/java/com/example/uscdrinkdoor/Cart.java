package com.example.uscdrinkdoor;


import java.util.ArrayList;

public class Cart {
    ArrayList<Item> cart = new ArrayList<Item>();


    public void addItem(Item item){
        cart.add(item);
    }

    public void removeItem(Item item){
        cart.remove(item);
    }

    public boolean checkItem(Item item){
        for (int i=0; i<cart.size(); i++){
            if (cart.get(i).getName().equals(item.getName())){
                return true;
            }
        }

        return false;
    }

    public int numItems(){
        return cart.size();
    }




}
