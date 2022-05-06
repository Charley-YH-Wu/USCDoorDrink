package com.example.uscdrinkdoor;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.uscdrinkdoor.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Order {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public boolean Completed; // "Active" or "Past"
    private int OrderID;
    private String userEmail;
    private String sellerEmail;

    private String userName;
    private String phone;
    private String address;
    private float total;

    private ShoppingCart cart;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public Order(String userEmail, String sellerEmail, List<Item> items){
        this.userEmail = userEmail;
        this.sellerEmail = sellerEmail;
        this.cart = new ShoppingCart(items);

        //set user data from db
        DocumentReference docRef =  db.collection("users").document(userEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        userName = (String) document.get("name");
                        phone = (String) document.get("phone");
                        address = (String) document.get("address");

                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }


            }
        });

        //calculate order total
        this.total = cart.calculateTotal();

    }

    public void Display_Order(){}

    public void Update_Status(){}

    public int Get_Order_ID(){ return this.OrderID; }

    public String Get_User_ID(){ return this.userEmail; }

    public String Get_Seller_ID(){ return this.sellerEmail; }

    public List<Item> Get_Items(){
        return new ArrayList<>();
    }




}