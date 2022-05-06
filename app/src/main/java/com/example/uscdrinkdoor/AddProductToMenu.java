package com.example.uscdrinkdoor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddProductToMenu extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static final String TAG = "AddProduct";



    private EditText price;
    private EditText name;
    private EditText description;
    private EditText caffeine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseUser currentUser = mAuth.getCurrentUser();


        Intent intent = getIntent();
        String editProdName = intent.getStringExtra("name");
        assert currentUser != null;
        String currentEmail = currentUser.getEmail();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_to_menu);

        name = findViewById(R.id.productName);
        price = findViewById(R.id.productPrice);
        caffeine = findViewById(R.id.productCaffeine);
        description = findViewById(R.id.productDescription);

        Button addProductbtn = findViewById(R.id.addProductbtn);


        //fill up form with saved data for when editing a product
       setEditForm(editProdName, currentEmail);

        //add or edit products to the menu
        addProductbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String ns = name.getText().toString();
               long ps = Long.parseLong(price.getText().toString());
               long cs =Long.parseLong(caffeine.getText().toString());
               String ds = description.getText().toString();

            //make sure fields are not empty
                if(ns.isEmpty() || ps==0 || cs==0 || ds.isEmpty()){
                    Toast.makeText(AddProductToMenu.this, "Make sure you have filled all fields", Toast.LENGTH_SHORT  ).show();
                }else{
                    Map<String, Object> product = new HashMap<>();
                    product.put("Name", ns);
                    product.put("Price", ps );
                    product.put("Caffeine", cs);
                    product.put("description", ds);
                    product.put("Email", currentUser.getEmail());

                    Log.d(TAG, "onClick: " + ns + ps + cs+ ds);

                    EspressoIdlingResource.increment();
                    assert currentEmail != null;
                    CollectionReference colRef =  db.collection("users").document(currentEmail).collection("Menu");
                    //save new product to db
                    saveNewProduct(colRef, ns, product);

                    //check for edited/double writing
                    checkForEdit(colRef, editProdName, ns);


                }

            }

        });
    }

    public void setEditForm(String editProdName, String currentEmail){
        if(editProdName != null) {
            EspressoIdlingResource.increment();

            DocumentReference docRef = db.collection("users").document(currentEmail).collection("Menu").document(editProdName);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                            updateUIonEdit(document);

                        } else {
                            Log.d("TAG", "No such document");
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }

                    EspressoIdlingResource.decrement();


                }


            });
        }
    }

    public void saveNewProduct(CollectionReference colRef, String ns, Map<String, Object> product){
        colRef.document(ns)
                .set(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Product successfully added!");
                        Toast.makeText(AddProductToMenu.this, "Product successfully added! ", Toast.LENGTH_SHORT  ).show();

                        updateUIonSave();
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

    public void checkForEdit(CollectionReference colRef, String editProdName, String ns){

        if(editProdName!=null ) {
            if (!editProdName.equals(ns)) {

                colRef.document(editProdName)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.w(TAG, "Product deleted successfully");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting product", e);
                            }
                        });
            }
        }
    }
    public void updateUIonSave(){
        name.setText("");
        price.setText("");
        description.setText("");
        caffeine.setText("");
    }

    public void updateUIonEdit(DocumentSnapshot document){
        name.setText((String)document.get("Name"));
        price.setText(document.get("Price").toString());
        description.setText((String)document.get("description"));
        caffeine.setText(document.get("Caffeine").toString());
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