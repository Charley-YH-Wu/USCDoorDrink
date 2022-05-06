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

public class CartItemAdapter extends ArrayAdapter<Item> {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    boolean store=false;


    private Context mContext;
    private int mResource;

    public CartItemAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Item> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userEmail = currentUser.getEmail();

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView name = convertView.findViewById(R.id.userName);
        TextView description = convertView.findViewById(R.id.Description);
        TextView price = convertView.findViewById(R.id.Price);
        TextView caffeine = convertView.findViewById(R.id.userCaffeine);

        String productName = getItem(position).getName();

        name.setText(productName);
        description.setText(getItem(position).getDescription());

        String priceString = "$" + String.valueOf(getItem(position).getPrice());
        price.setText(priceString);

        String caffeineString = String.valueOf(getItem(position).getCaffeine()) + " mg";
        caffeine.setText(caffeineString);

        return convertView;
    }



}
