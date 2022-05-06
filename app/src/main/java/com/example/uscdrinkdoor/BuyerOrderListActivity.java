package com.example.uscdrinkdoor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BuyerOrderListActivity extends AppCompatActivity implements BuyerOrderIDAdapter.BuyerOrderClickListener{
    ListView listview;

    final Context context = this;

    private static final String TAG = "BuyerOrderListActivity";

    ArrayList<String> orderIDs = new ArrayList<String>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String emailAddress = currentUser.getEmail();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_order_list);

        listview = findViewById(R.id.listView);

        EspressoIdlingResource.increment();

        CollectionReference colRef = db.collection("users").document(emailAddress).collection("Past Orders");
        colRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                orderIDs.add((String)document.getId());
                            }

                            BuyerOrderIDAdapter itemAdapter = new BuyerOrderIDAdapter(context, R.layout.order_row, orderIDs);

                            listview.setAdapter(itemAdapter);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        EspressoIdlingResource.decrement();

                    }

                });

    }

    public void viewOrder(String id) {
        //send intent to wherever next
        Intent view = new Intent(this, BuyerOrderPage.class).putExtra("id", id);
        startActivity(view);
    }

    public void viewChart(View view){
        Intent intent = new Intent(this, ChartActivity.class);
        startActivity(intent);
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
