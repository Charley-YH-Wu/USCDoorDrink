package com.example.uscdrinkdoor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SellerMenu extends AppCompatActivity implements ItemAdapter.ItemClickListener{

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static final String TAG = "SellerMenu";
    boolean storeAccount;

    ListView listview;

    final Context context = this;

    //instantiate list for the seller.menu for layout
    ArrayList<Item> menu = new ArrayList<Item>();
    String emailAddress;
    int estimated_time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentEmail = currentUser.getEmail();

        Intent intent = getIntent();
        emailAddress = intent.getStringExtra("email");
        estimated_time = intent.getIntExtra("Delivery_Time",0);


        if(emailAddress == null){
            emailAddress = currentEmail;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        listview = findViewById(R.id.listView);

        Button checkout = findViewById(R.id.Checkout);

        Button btn = findViewById(R.id.Menu);

        EspressoIdlingResource.increment();

        DocumentReference docRef = db.collection("users").document(currentEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        storeAccount = (boolean) document.get("store");

                        if (storeAccount){
                            checkout.setText("Add Product");
                            if(!currentEmail.equals(emailAddress)){
                                checkout.setVisibility(View.GONE);
                            }
                        }
                        else{
                            checkout.setText("Checkout");
                            btn.setText("Cart");
                        }

                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }

                EspressoIdlingResource.decrement();

            }
        });

        EspressoIdlingResource.increment();

        db.collection("users").document(emailAddress).collection("Menu")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                menu.add(new Item((String) document.get("Name"), (String)document.get("description"), (long) document.get("Price"), (long) document.get("Caffeine"), (String) document.get("Email")));
                            }

                            ItemAdapter itemAdapter = new ItemAdapter(context, R.layout.menu_row, menu);

                            listview.setAdapter(itemAdapter);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        EspressoIdlingResource.decrement();

                    }

                });


            checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent;

                    if(storeAccount){
                        intent = new Intent(SellerMenu.this, AddProductToMenu.class);
                    }else{
                        intent = new Intent(SellerMenu.this, ShoppingCartActivity.class).putExtra("Delivery_Time",estimated_time);
                    }
                    startActivity(intent);

                }
            });

//        Button account = findViewById(R.id.Account_Profile);
//        //account button
//        account.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent;
//                if(storeAccount){
//                    intent = new Intent(SellerMenu.this, Seller_Profile.class);
//                }
//                else{
//                    intent = new Intent(SellerMenu.this, User_Profile.class);
//                }
//                startActivity(intent);
//
//            }
//        });

//        //cart or seller menu button
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent;
//                if(storeAccount){
//                    intent = new Intent(SellerMenu.this, SellerMenu.class);
//                }
//                else{
//                    intent = new Intent(SellerMenu.this, ShoppingCart.class);
//                }
//                startActivity(intent);
//
//            }
//        });



    }

    @Override
    public void onAddToCartClick(String name, String userEmail) {
        Item toAdd = null;

        for (int i=0; i<menu.size(); i++){
            if (menu.get(i).getName().equals(name)){
                toAdd = menu.get(i);
            }
        }
        UUID uuid = UUID.randomUUID();

        Map<String, Object> product = new HashMap<>();
        product.put("Name", toAdd.getName());
        product.put("Price", toAdd.getPrice() );
        product.put("Caffeine", toAdd.getCaffeine());
        product.put("description", toAdd.getDescription());
        product.put("Email", toAdd.getSellerEmail());

        EspressoIdlingResource.increment();
        //save new product to db
        db.collection("users").document(userEmail).collection("Cart").document(uuid.toString())
                .set(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Product successfully added!");
                        Toast.makeText(SellerMenu.this, "Product added to cart! ", Toast.LENGTH_SHORT  ).show();
                        EspressoIdlingResource.decrement();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding product", e);
                        EspressoIdlingResource.decrement();

                    }
                });

    }
    @Override
    public void sendToEdit(String name) {
        Intent productEdit = new Intent(this, AddProductToMenu.class).putExtra("name", name);
        startActivity(productEdit);
    }

    public void clickAccount(View view) {
        Intent intent;
        if(storeAccount){
            intent = new Intent(SellerMenu.this, Seller_Profile.class);
        }
        else{
            intent = new Intent(SellerMenu.this, User_Profile.class);
            intent.putExtra("Delivery_Time",estimated_time);
        }
        startActivity(intent);
    }

    public void clickMenu(View view) {
        Intent intent;
        if(storeAccount){
            intent = new Intent(SellerMenu.this, SellerMenu.class);
        }
        else{
            intent = new Intent(SellerMenu.this, ShoppingCartActivity.class);
            intent.putExtra("Delivery_Time",estimated_time);
        }
        startActivity(intent);
    }

    public void clickOrder(View view) {
        Intent intent;
        if(storeAccount){
            intent = new Intent(SellerMenu.this, SellerOrderListActivity.class);
        }
        else{
            intent = new Intent(SellerMenu.this, OrderCompleteActivity.class);
            intent.putExtra("Delivery_Time",estimated_time);
        }
        startActivity(intent);
    }

    public void clickHome(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("Delivery_Time",estimated_time);
        startActivity(intent);
    }



}
