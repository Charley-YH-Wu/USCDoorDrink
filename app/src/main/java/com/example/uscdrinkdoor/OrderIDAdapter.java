package com.example.uscdrinkdoor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class OrderIDAdapter extends ArrayAdapter<String> {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    boolean store=false;


    private Context mContext;
    private int mResource;
    private OrderClickListener clickListener;

    public OrderIDAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.clickListener = (OrderClickListener) context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userEmail = currentUser.getEmail();

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        convertView = layoutInflater.inflate(mResource, parent, false);;

        //order ID
        TextView orderIDText = convertView.findViewById(R.id.orderID);

        //grab order ID
        String orderID = getItem(position);

        //fill order ID
        orderIDText.setText(orderID);

        //Button
        Button view = convertView.findViewById(R.id.View);
        Boolean completed = false;

        //check if order is complete and set button text accordingly
       DocumentReference docRef =  db.collection("users").document(userEmail).collection("Orders").document(orderID);

       updateButtonText(docRef, view);




        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.viewOrder(orderID);

            }
        });



        return convertView;
    }

    private void updateButtonText(DocumentReference docRef, Button view) {
        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if( task.getResult().get("Completed") != null){
                                if ((Boolean)task.getResult().get("Completed")){
                                    view.setText("Order Completed");
                                }else{
                                    view.setText("View Order");
                                }

                            }
                        }

                    }
                });
    }

    public interface OrderClickListener {
        public void viewOrder(String id);
    }


}
