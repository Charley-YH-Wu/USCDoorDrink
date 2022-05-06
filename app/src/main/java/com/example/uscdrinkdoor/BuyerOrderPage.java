package com.example.uscdrinkdoor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.Timestamp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class BuyerOrderPage extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static final String TAG = "BuyerOrderInfoPage";
    private TextView orderTitle;
    private TextView orderCurrent;
    private TextView orderCaffeine;
    private TextView orderDate;
    private TextView orderDeliveryTime;
    private TextView orderTotal;

    private Button goBack;


    private ListView listview;

    Timestamp orderTime;

    String orderID;

    Context context = this;

    ArrayList<orderItem> order = new ArrayList<orderItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_order_page);

        String currentEmail = mAuth.getCurrentUser().getEmail();
        orderID = getIntent().getStringExtra("id");


        orderTitle = findViewById(R.id.orderTitle);

        orderCurrent = findViewById(R.id.orderCurrent);
        orderDate = findViewById(R.id.orderDate);
        orderCaffeine = findViewById(R.id.orderCaffeine);
        orderDeliveryTime = findViewById(R.id.orderDeliveryTime);
        orderTotal = findViewById(R.id.orderTotal);

        orderTitle.append(" " + orderID);

        listview = findViewById(R.id.orderLv);

        goBack = findViewById(R.id.orderBack);

        EspressoIdlingResource.increment();

        //HANDLE ORDER FOR BUYER SIDE
        DocumentReference docRef =  db.collection("users").document(currentEmail).collection("Past Orders").document(orderID);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());

                        updateUI(documentSnapshot);
                        EspressoIdlingResource.decrement();

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error reading document", e);
                        EspressoIdlingResource.decrement();

                    }
                });

        EspressoIdlingResource.increment();

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    orderTime = task.getResult().getTimestamp("Date");
                    assert orderTime != null;
                    orderDate.append(orderTime.toDate().toString());

                }
            }
        });

        docRef.collection("Products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                order.add(new orderItem((String) document.get("Product Name"), (long)document.get("Price"), (String)document.get("Description")));
                            }
//

                            OrderPageAdapter itemAdapter = new OrderPageAdapter(context, R.layout.orderpage_row, order);
//
                            listview.setAdapter(itemAdapter);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        EspressoIdlingResource.decrement();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        EspressoIdlingResource.decrement();

                    }
                });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BuyerOrderPage.this, BuyerOrderListActivity.class);
                startActivity(intent);
            }
        });

        EspressoIdlingResource.increment();

    }

    public void updateUI(DocumentSnapshot document){

        orderCurrent.append(document.get("Current").toString());
        //orderDate.append(document.get("Date").toDate().toString());
        //orderDate.append(orderTime.toString());
        orderCaffeine.append(document.get("Order Caffeine").toString() + " mg");
        orderDeliveryTime.append(document.get("Time to deliver").toString() + " minutes");
        orderTotal.append(document.get("Order Total").toString());
    }

    public void clickAccount(View view) {
        Intent intent = new Intent(this, User_Profile.class);
        startActivity(intent);
    }

    public void clickCart(View view) {
        Intent intent = new Intent(this, ShoppingCartActivity.class);
        startActivity(intent);
    }

    public void clickOrder(View view){
        Intent intent = new Intent(this, OrderCompleteActivity.class);
        startActivity(intent);
    }

    public void clickHome(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}