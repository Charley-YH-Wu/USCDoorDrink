package com.example.uscdrinkdoor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
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

import java.math.BigInteger;
import java.sql.Array;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShoppingCartActivity extends AppCompatActivity{
    ListView listview;

    final Context context = this;

    private static final String TAG = "ShoppingCartActivity";

    ArrayList<Item> cart = new ArrayList<Item>();

    Button submitOrder;

    Order newOrder;

    String sellerEmail;
    
    int dailyCaffeine;
    boolean cont = false;


    Map<String, Object> pastOrder = new HashMap<>();

    int orderCaffeine = 0;
    int orderTotal =0;
    int estimated_time =0;
    
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        estimated_time = intent.getIntExtra("Delivery_Time",0);


        FirebaseUser currentUser = mAuth.getCurrentUser();
        String emailAddress = currentUser.getEmail();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);


        listview = findViewById(R.id.listView);
        submitOrder = findViewById(R.id.Submit_Order);

        EspressoIdlingResource.increment();
        //access items added to cart by user
        db.collection("users").document(emailAddress).collection("Cart")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                cart.add(new Item((String) document.get("Name"), (String)document.get("description"), (long) document.get("Price"), (long) document.get("Caffeine"), (String) document.get("Email")));
                                sellerEmail = (String) document.get("Email");
                            }


                            CartItemAdapter itemAdapter = new CartItemAdapter(context, R.layout.cart_row, cart);

                            listview.setAdapter(itemAdapter);

                            newOrder = new Order(emailAddress, sellerEmail, cart);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        EspressoIdlingResource.decrement();
                    }

                });
        




        submitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //FEATURE #2: Alexandros Hatzopoulos
                //check if user has had too much caffeine
                if( !checkCaffeine(emailAddress) ) {
                    Log.d(TAG, "onClick: Caffeine check returns");
                    return;
                }else {

                    //get buyer's information to pass on to seller

                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                    //record order data to send to seller db
                    Map<String, Object> order = new HashMap<>();
                    order.put("Customer Name", newOrder.getUserName());
                    order.put("Customer Email", newOrder.getUserEmail());
                    order.put("Customer Phone", newOrder.getPhone());
                    order.put("Delivery Address", newOrder.getAddress());
                    order.put("Completed", false);
                    order.put("Time Ordered", sdf3.format(timestamp));
                    order.put("Total Amount", newOrder.getTotal());
                    order.put("Time to deliver", "-");

                    String uuid = String.format("%040d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));
                    String uuid16digits = uuid.substring(uuid.length() - 16);


                    EspressoIdlingResource.increment();

                    //transfer current cart to past orders
                    CollectionReference colRef = db.collection("users").document(emailAddress).collection("Cart");
                    colRef.get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                            pastOrder.put("Product Name", document.get("Name"));
                                            pastOrder.put("Price", document.get("Price"));
                                            pastOrder.put("Caffeine", document.get("Caffeine"));
                                            pastOrder.put("Description", document.get("description"));
                                            pastOrder.put("Time Ordered", timestamp);
                                            pastOrder.put("Time to deliver", "Not delivered yet");


                                            orderCaffeine += (long) document.get("Caffeine");
                                            orderTotal += (long) document.get("Price");
                                            //add the cart to past orders
                                            db.collection("users").document(emailAddress).collection("Past Orders").document(uuid16digits).collection("Products").document((String) document.get("Name"))
                                                    .set(pastOrder)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Log.d(TAG, "Item successfully added to past orders!");

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d(TAG, "Item failed to add to past orders!");

                                                        }
                                                    });
                                            pastOrder.clear();
                                            //Deleting product from current user cart
                                            colRef.document(document.getId()).delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error deleting document", e);
                                                        }
                                                    });

                                        }

                                        //Updating order with general info
                                        Map<String, Object> pastOrderInfo = new HashMap<>();
                                        pastOrderInfo.put("Order Caffeine", orderCaffeine);
                                        pastOrderInfo.put("Order Total", orderTotal);
                                        pastOrderInfo.put("Date", timestamp);
                                        pastOrderInfo.put("Current", true);
                                        pastOrderInfo.put("Time to deliver", "Not delivered yet");

                                        db.collection("users").document(emailAddress).collection("Past Orders").document(uuid16digits)
                                                .set(pastOrderInfo)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Log.d(TAG, "Info successfully added to past orders!");

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(TAG, "Info failed to add to past orders!");

                                                    }
                                                });


                                    }

                                    EspressoIdlingResource.decrement();
                                }
                            });

                    EspressoIdlingResource.increment();
                    //save new order to db on seller side
                    db.collection("users").document(sellerEmail).collection("Orders").document(uuid16digits)
                            .set(order)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {

                                public void onSuccess(Void unused) {
                                    //must add products ordered to nested collection within order
                                    Map<String, Object> products = new HashMap<>();
                                    for (Item i : cart) {
                                        products.put("Name", i.getName());
                                        products.put("description", i.getDescription());
                                        products.put("Price", i.getPrice());

                                        db.collection("users").document(sellerEmail).collection("Orders")
                                                .document(uuid16digits.toString()).collection("Products")
                                                .add(products)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Log.d(TAG, "Item successfully added to order!");

                                                    }
                                                });
                                    }
                                    products.clear();
                                    Intent orderComplete = new Intent(ShoppingCartActivity.this, OrderCompleteActivity.class).putExtra("Delivery_Time", estimated_time);
                                    startActivity(orderComplete);


                                    Log.d(TAG, "Order successfully added!");
                                    Toast.makeText(ShoppingCartActivity.this, "Order successfully sent! ", Toast.LENGTH_SHORT).show();
                                    //send user to order complete page
                                    //                    updateUI();
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
            }
        });



    }


    public boolean checkCaffeine(String email){


        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("You seem to have been drinking too much coffee (over 400mg in the past 24hours!). Are you sure you want to continue?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       cont =true;
                        dialog.cancel();

                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        cont = false;
                        dialog.cancel();
                    }
                });


        EspressoIdlingResource.increment();
        db.collection("users").document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if(document.get("Caffeine")!=null ){
                                if((long)document.get("Caffeine") > 400){
                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();
                                }else{
                                    cont=true;
                                }
                            }else {
                                cont = true;
                            }
                        }
                        EspressoIdlingResource.decrement();

                    }
                });
        return cont;
    }


    public void updateUI(){
        Intent complete = new Intent(this, MapsActivity.class);
        startActivity(complete);
    }

    public void clickAccount(View view) {
        Intent intent = new Intent(this, User_Profile.class);
        intent.putExtra("Delivery_Time",estimated_time);
        startActivity(intent);
    }

    public void clickOrder(View view){
        Intent intent = new Intent(this, OrderCompleteActivity.class);
        intent.putExtra("Delivery_Time",estimated_time);
        startActivity(intent);
    }

    public void clickCart(View view) {
        Intent intent = new Intent(this, ShoppingCartActivity.class);
        intent.putExtra("Delivery_Time",estimated_time);
        startActivity(intent);
    }

    public void clickHome(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("Delivery_Time",estimated_time);
        startActivity(intent);
    }

}
