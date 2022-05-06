package com.example.uscdrinkdoor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Seller_Profile extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private Button sellerName;
    private Button sellerAddress;
    private Button sellerEmail;
    private Button sellerMenu;
    private Button sellerWallet;
    private Button sellerOrders;
    private Button sellerLogoff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);

        sellerAddress = findViewById(R.id.userAddress);
        sellerName = findViewById(R.id.userName);
        sellerEmail = findViewById(R.id.sellerEmail);
        sellerMenu = findViewById(R.id.sellerMenu);
        sellerWallet = findViewById(R.id.sellerWallet);
        sellerOrders = findViewById(R.id.sellerOrders);
        sellerLogoff = findViewById(R.id.userLogoff);


        EspressoIdlingResource.increment();
        DocumentReference docRef =  db.collection("users").document(currentUser.getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        updateUI(document);

                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
                EspressoIdlingResource.decrement();


            }



        });

        sellerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent menu = new Intent(Seller_Profile.this, SellerMenu.class);
                startActivity(menu);
            }
        });

        sellerWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent wallet = new Intent(Seller_Profile.this, sellerWallet.class);
                startActivity(wallet);
            }
        });

        sellerLogoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent logOut = new Intent(Seller_Profile.this, LoginActivity.class);
                startActivity(logOut);
            }
        });




    }
    public void updateUI(DocumentSnapshot document){
        sellerAddress.setText((String)document.get("address"));
        sellerName.setText((String)document.get("name"));
        sellerEmail.setText((String)document.get("emailAddress"));
       //add phone number
    }

    public void clickAccount(View view) {
        Intent intent = new Intent(this, Seller_Profile.class);
        startActivity(intent);
    }

    public void clickMenu(View view) {
        Intent intent = new Intent(this, SellerMenu.class);
        startActivity(intent);
    }

    public void viewOrders(View view){
        Intent intent = new Intent(this, SellerOrderListActivity.class);
        startActivity(intent);
    }

    public void clickHome(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}