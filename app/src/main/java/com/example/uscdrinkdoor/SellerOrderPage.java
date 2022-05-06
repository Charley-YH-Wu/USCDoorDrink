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

public class SellerOrderPage extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static final String TAG = "OrderInfoPage";
    private TextView orderTitle;
    private TextView orderCompleted;
    private TextView orderName;
    private TextView orderAddress;
    private TextView orderPhone;
    private TextView orderDeliveryTime;
    private TextView orderTime;
    private TextView orderTotal;

    private Button completeOrder;
    private Button goBack;


    private ListView listview;

    String orderID;

    Context context = this;

    ArrayList<orderItem> order = new ArrayList<orderItem>();
    String customerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_order_page);

        String currentEmail = mAuth.getCurrentUser().getEmail();
        orderID = getIntent().getStringExtra("id");


        orderTitle = findViewById(R.id.orderTitle);
        orderCompleted = findViewById(R.id.orderCompleted);
        orderName = findViewById(R.id.orderCustomer);
        orderAddress = findViewById(R.id.orderAddress);
        orderPhone = findViewById(R.id.orderPhone);
        orderDeliveryTime = findViewById(R.id.orderDeliveryTime);
        orderTime = findViewById(R.id.orderTime);
        orderTotal = findViewById(R.id.orderTotal);

        orderTitle.append(" " + orderID);

        listview = findViewById(R.id.orderLv);

        completeOrder = findViewById(R.id.orderComplete);
        goBack = findViewById(R.id.orderBack);

        EspressoIdlingResource.increment();

        //HANDLE ORDER FOR SELLER SIDE
        DocumentReference docRef =  db.collection("users").document(currentEmail).collection("Orders").document(orderID);
                docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());
                        if((boolean)documentSnapshot.get("Completed") == true){
                            completeOrder.setEnabled(false);
                        }

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

        docRef.collection("Products")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                            order.add(new orderItem((String) document.get("Name"), (long)document.get("Price"), (String)document.get("description")));
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
                        Intent intent = new Intent(SellerOrderPage.this, SellerOrderListActivity.class);
                        startActivity(intent);
                    }
                });

        EspressoIdlingResource.increment();

        //HANDLE ORDER FOR USER SIDE -> COMPLETED/PAST
                completeOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        docRef.update("Completed",true);
                        //calculate time it took for order to be delivered
                        DocumentReference userDocRef = db.collection("users").document(customerEmail).collection("Past Orders").document(orderID);


                        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    Timestamp orderTime = task.getResult().getTimestamp("Date");
                                    assert orderTime != null;

                                    long currentTime = System.currentTimeMillis();
                                    long timestampSeconds = TimeUnit.MILLISECONDS.toSeconds(currentTime);

                                    long timeDiff = timestampSeconds - orderTime.getSeconds();
                                    long currentMinutes = timeDiff/60;

                                    docRef.update("Time to deliver", currentMinutes);

                                    //update on user side

                                    userDocRef.update("Current", false);
                                    userDocRef.update("Time to deliver", currentMinutes);

                                    Intent intent = new Intent(SellerOrderPage.this, SellerOrderPage.class).putExtra("id",orderID);

                                    startActivity(intent);
                                    EspressoIdlingResource.decrement();
                                }
                            }
                        });


                        //Time Ordered


                      }

                });

    }

    public void updateUI(DocumentSnapshot document){
        customerEmail = document.get("Customer Email").toString();

        orderCompleted.append(document.get("Completed").toString());

        orderName.append(document.get("Customer Name").toString());
        orderAddress.append(document.get("Delivery Address").toString());
        orderPhone.append(document.get("Customer Phone").toString());
        orderDeliveryTime.append(document.get("Time to deliver").toString() + " minutes");
        orderTotal.append(document.get("Total Amount").toString());
        orderTime.append(document.get("Time Ordered").toString());
    }

    public void clickAccount(View view) {
        Intent intent = new Intent(this, Seller_Profile.class);
        startActivity(intent);
    }

    public void clickMenu(View view) {
        Intent intent = new Intent(this, SellerMenu.class);
        startActivity(intent);
    }

    public void clickOrder(View view){
        Intent intent = new Intent(this, SellerOrderListActivity.class);
        startActivity(intent);
    }

    public void clickHome(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}