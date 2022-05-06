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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class User_Profile extends AppCompatActivity{

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private Button userName;
    private Button userAddress;
    private Button userEmail;
    private Button userOrder;
    private Button userCaffeine;
    private Button userPhone;
    private Button userLogoff;

    long caffeine=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        userName = findViewById(R.id.userName);
        userAddress = findViewById(R.id.userAddress);
        userEmail  = findViewById(R.id.userEmail);
        userOrder  = findViewById(R.id.userOrder);
        userCaffeine = findViewById(R.id.userCaffeine);
        userLogoff  = findViewById(R.id.userLogoff) ;
        userPhone = findViewById(R.id.userPhone);


        EspressoIdlingResource.increment();

        DocumentReference docRef =  db.collection("users").document(currentUser.getEmail());

        //get general order data and display on screen
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());

                        updateUI(document);

                        //FEATURE #2: Alexandros Hatzopoulos
                        updateCaffeine(docRef);

                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }

                EspressoIdlingResource.decrement();
            }



        });

        userLogoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent logOut = new Intent(User_Profile.this, LoginActivity.class);
                startActivity(logOut);
            }
        });

    }
    public void updateUI(DocumentSnapshot document){
        userAddress.setText((String)document.get("address"));
        userName.setText((String)document.get("name"));
        userEmail.setText((String)document.get("emailAddress"));
        userPhone.setText((String)document.get("phone"));

    }

    public void updateCaffeine(DocumentReference docRef){
        //FEATURE #2: Alexandros Hatzopoulos

        long currentTime = System.currentTimeMillis();
        long timestampSeconds = TimeUnit.MILLISECONDS.toSeconds(currentTime);


        EspressoIdlingResource.increment();
        docRef.collection("Past Orders")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            com.google.firebase.Timestamp orderTime = document.getTimestamp("Date");

                            assert orderTime != null;
                            long timeDiff = timestampSeconds - orderTime.getSeconds();
                            long currentMinutes = timeDiff/60;
                            long currentHours = currentMinutes/60;

                            if(currentHours <24 ){
                                caffeine+=(long)document.get("Order Caffeine");
                                docRef.update("Caffeine", caffeine);
                            }
                        }
                    }
                    userCaffeine.setText("Caffeine Intake:" + caffeine);
                    EspressoIdlingResource.decrement();
                }
            });
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
        Intent intent = new Intent(this, BuyerOrderListActivity.class);
        startActivity(intent);
    }

    public void clickHome(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }


}
