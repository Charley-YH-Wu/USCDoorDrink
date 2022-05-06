package com.example.uscdrinkdoor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Array;
import java.util.Date;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.Timestamp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ChartActivityWeek extends AppCompatActivity {

    FirebaseFirestore db;
    private FirebaseAuth mAuth;


    private static final String TAG = "ChartActivity";

    String currentEmail;
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        db =  FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentEmail = mAuth.getCurrentUser().getEmail();
        pieChart = findViewById(R.id.pieChart);

        retrieveData();
    }

    public void retrieveData(){
        long currentTime = System.currentTimeMillis();
        long timestampSeconds = TimeUnit.MILLISECONDS.toSeconds(currentTime);


        CollectionReference colRef = db.collection("users").document(currentEmail).collection("Past Orders");
        colRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        ArrayList<PieEntry> chartInfo = new ArrayList<PieEntry>();

                        if (task.isSuccessful()) {
                            long totalCaffeine = 0;
                            long totalCost = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                com.google.firebase.Timestamp orderTime = document.getTimestamp("Date");

                                long timeDiff = timestampSeconds - orderTime.getSeconds();
                                long currentMinutes = timeDiff/60;
                                long currentHours = currentMinutes/60;

                                if(currentHours <= 168 ){
                                    totalCaffeine += (Long) document.get("Order Caffeine");
                                    totalCost += (Long) document.get("Order Total");
                                }

                            }


                            chartInfo.add(new PieEntry(totalCost, "Total Cost ($)"));
                            chartInfo.add(new PieEntry(totalCaffeine, "Total Caffeine (mg)"));

                            showChart(chartInfo);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }

                });
    }

    public void showChart(ArrayList<PieEntry> chartInfo){
        PieDataSet pieDataSet = new PieDataSet(chartInfo, "Total Caffeine and Cost History");



        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setNoDataText("No Orders Within Past Day");

        pieChart.setData(pieData);
        pieChart.invalidate();
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Total Caffeine and Order Cost History");

        pieChart.animate();
    }

    public void clickDay(View view) {
        Intent intent = new Intent(this, ChartActivity.class);
        startActivity(intent);
    }

    public void clickWeek(View view) {
        Intent intent = new Intent(this, ChartActivityWeek.class);
        startActivity(intent);
    }

    public void clickMonth(View view){
        Intent intent = new Intent(this, ChartActivityMonth.class);
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



