package com.example.uscdrinkdoor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class OrderCompleteActivity extends AppCompatActivity {
    ListView listview;

    private TextView totalText;

    final Context context = this;

    private static final String TAG = "OrderCompleteActivity";

    ArrayList<Item> cart = new ArrayList<Item>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    Integer estimated_time = 0;
    long caffeine = 0;

    long totalCost = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_complete);

        Intent intent = getIntent();
        estimated_time = intent.getIntExtra("Delivery_Time",0);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String emailAddress = currentUser.getEmail();
        TextView t = findViewById(R.id.time);
        totalText = findViewById(R.id.summary);
        t.append(" " +estimated_time.toString());


        listview = findViewById(R.id.listView);


        updateCaffeine(emailAddress);

        EspressoIdlingResource.increment();
        //show items only from current order
        CollectionReference colRef = db.collection("users").document(emailAddress).collection("Past Orders");
                colRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //check if current
                                if ((boolean) document.get("Current")) {
                                    totalCost += (Long) document.get("Order Total");
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    colRef.document(document.getId()).collection("Products").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        //populate list of ordered items
                                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                                            cart.add(new Item((String) document.get("Product Name"), (String) document.get("Description"), (long) document.get("Price"), (long) document.get("Caffeine"), (String) document.get("Email")));

                                                        }
                                                        CartItemAdapter itemAdapter = new CartItemAdapter(context, R.layout.cart_row, cart);

                                                        listview.setAdapter(itemAdapter);
                                                    }
                                                }
                                            });

                                }
                            }

                            totalText.append(" " + Long.toString(totalCost)+"$");


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        EspressoIdlingResource.decrement();
                    }

                });


    }


    public void updateCaffeine(String emailAddress){
        //FEATURE #2: Alexandros Hatzopoulos

        long currentTime = System.currentTimeMillis();
        long timestampSeconds = TimeUnit.MILLISECONDS.toSeconds(currentTime);



        EspressoIdlingResource.increment();
        db.collection("users").document(emailAddress).collection("Past Orders").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                com.google.firebase.Timestamp orderTime = document.getTimestamp("Date");

                                long timeDiff = timestampSeconds - orderTime.getSeconds();
                                long currentMinutes = timeDiff/60;
                                long currentHours = currentMinutes/60;

                                if(currentHours <24 ){
                                    caffeine += (long)document.get("Order Caffeine");
                                    db.collection("users").document(emailAddress).update("Caffeine", caffeine);
                                }
                            }
                        }

                        EspressoIdlingResource.decrement();
                    }
                });
    }
    public void clickAccount(View view) {
        Intent intent = new Intent(this, User_Profile.class);
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
