package com.example.uscdrinkdoor;

import com.example.uscdrinkdoor.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {


    private int CartID;
    private int UserID;
    private int SellerID;
    private List<Item> items;

    public ShoppingCart(List<Item> items){
        this.items = items;
    }

    public void Add_Item(Item i){items.add(i);}

    public void Remove_Item(Item i){items.remove(i);}

    public void Display_Cart(){}

    public int Get_User_ID(){ return UserID; }

    public int Get_Seller_ID(){ return SellerID; }

    public int Get_Cart_ID(){ return CartID; }

    public List<Item> Get_Items(){
        return items;
    }

    public float calculateTotal(){
        float total = 0;
        for(Item i: items){
            total += i.getPrice();
        }
        return total;
    }


}