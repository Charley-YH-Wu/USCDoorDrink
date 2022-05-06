package com.example.uscdrinkdoor;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * An activity that displays a map showing the place at the device's current location.
 */
public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, TaskLoadedCallback {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public boolean store = false;

    private static final String TAG = MapsActivity.class.getSimpleName();
    public GoogleMap map;
    private CameraPosition cameraPosition;

    // The entry point to the Places API.
    private PlacesClient placesClient;

    private Polyline currentPolyline;
    private int estimated_time = 0;


    // The entry point to the Fused com.example.uscdrinkdoor.Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // Default location (USC) and default zoom when location permission not granted
    private final LatLng defaultLocation = new LatLng(34.0224, -118.2851);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused com.example.uscdrinkdoor.Location Provider.
    private Location lastKnownLocation;

    private Marker clicked;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    FirebaseUser currentUser;
    String userEmail;
    ArrayList<String> orderIDs = new ArrayList<String>();
    ArrayList<orderItem> order = new ArrayList<orderItem>();
    Button Recommend_Button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        currentUser = mAuth.getCurrentUser();
        userEmail = currentUser.getEmail();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        DocumentReference docRef = db.collection("users").document(userEmail);
        EspressoIdlingResource.increment();

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        if(document.get("store")!=null){
                            store = (boolean) document.get("store");
                        }
                        if (store == false){
                            Button btn = (Button) findViewById(R.id.sellerMenu);
                            btn.setText("Cart");
                            Recommend_Button = (Button) findViewById(R.id.recommend);
                            Recommend_Button.setVisibility(View.VISIBLE);
                        }
                    } else { Log.d("TAG", "No such document"); }
                } else { Log.d("TAG", "get failed with ", task.getException()); }
            }
        });


        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), "AIzaSyDewc_xqcDgxGJNJAEb0D3ipsKtxD3KqOI");
        placesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }


    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My com.example.uscdrinkdoor.Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        // Get nearby stores
        SearchNearby();

        RetrieveOrderHistory();

        EspressoIdlingResource.decrement();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
//                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void SearchNearby(){
        EspressoIdlingResource.increment();
        //Get store coordinates from database
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                LatLng storeLocation;
                                if((boolean) document.get("store")){
                                    double lat = (double) document.get("lat");
                                    double longitude = (double) document.get("long");
                                    storeLocation = new LatLng(lat, longitude);

                                    Marker marker = map.addMarker(new MarkerOptions().position(storeLocation).title((String) document.get("name")));
                                    marker.setSnippet("Click twice to see menu");
                                    marker.setTag((String)document.get("emailAddress"));
                                }
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        EspressoIdlingResource.decrement();
                    }
                });
        map.setOnMarkerClickListener(this);
    }


    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        EspressoIdlingResource.increment();
        Button drive = (Button) findViewById(R.id.driving);
        Button walk = (Button) findViewById(R.id.walking);
        drive.setVisibility(View.VISIBLE);
        walk.setVisibility(View.VISIBLE);

        if(clicked == null){
            clicked = marker;
            getRoute(marker, "driving");
        }
        else if (clicked.equals(marker)){
            String sellerEmail = clicked.getTag().toString();
            clicked = null;
            Intent intent = new Intent(this, SellerMenu.class).putExtra("email", sellerEmail);
            intent.putExtra("Delivery_Time",estimated_time);
            startActivity(intent);
        }
        else {
            clicked = marker;
            getRoute(marker, "driving");
        }
        EspressoIdlingResource.decrement();
        return false;
    }

    private void getRoute(Marker marker, String transport) {
        EspressoIdlingResource.increment();
        // marker.setSnippet(Estimated Delivery Time)
        String origin = "origin=" + lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();
        String dest = "&destination=" + marker.getPosition().latitude + "," + marker.getPosition().longitude;
        String mode = "&mode=" + transport;
        String key = "&key=AIzaSyDewc_xqcDgxGJNJAEb0D3ipsKtxD3KqOI";
        String urlrequest = "https://maps.googleapis.com/maps/api/directions/json?" + origin + dest + mode + key;
        new FetchURL(MapsActivity.this).execute(urlrequest, transport);
        EspressoIdlingResource.decrement();
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = map.addPolyline((PolylineOptions) values[0]);
    }

    @Override
    public void onSecondTaskDone(Object... values) {
        if(estimated_time != 0){
            estimated_time = 0;
        }
        estimated_time = Integer.parseInt((String) values[0]);
        Button btn = (Button) findViewById(R.id.esttime);
        btn.setVisibility(View.VISIBLE);
        btn.setText("Estimated Delivery Time: " + estimated_time + " mins");
    }

    public void clickAccount(View view) {
        Intent intent;
        if(store){
            intent = new Intent(MapsActivity.this, Seller_Profile.class);
        }
        else{
            intent = new Intent(MapsActivity.this, User_Profile.class);
            intent.putExtra("Delivery_Time",estimated_time);
        }
        startActivity(intent);
    }

    public void clickMenu(View view) {
        Intent intent;
        if(store){
            intent = new Intent(MapsActivity.this, SellerMenu.class);
        }
        else{
            intent = new Intent(MapsActivity.this, ShoppingCartActivity.class);
            intent.putExtra("Delivery_Time",estimated_time);
        }
        startActivity(intent);
    }

    public void clickOrder(View view){
        Intent intent;
        if(store){
            intent = new Intent(MapsActivity.this, SellerOrderListActivity.class);
        }
        else{
            intent = new Intent(MapsActivity.this, OrderCompleteActivity.class);
            intent.putExtra("Delivery_Time",estimated_time);
        }
        startActivity(intent);
    }

    public void SelectWalking(View view) {
        getRoute(clicked, "walking");
    }

    public void SelectDriving(View view) { getRoute(clicked, "driving"); }

    public void RetrieveOrderHistory() {
        CollectionReference colRef = db.collection("users").document(userEmail).collection("Past Orders");
        colRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String s = (String)document.getId();
                                orderIDs.add(s);
                                RetrieveProduct(s);
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void RetrieveProduct(String orderID) {
        DocumentReference docRef = db.collection("users").document(userEmail).collection("Past Orders").document(orderID);
        docRef.collection("Products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                order.add(new orderItem((String) document.get("Product Name"), (long) document.get("Price"), (String) document.get("Description")));
                            }
                        } else { Log.d(TAG, "Error getting documents: ", task.getException()); }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) { }
                });
    }

    public void Recommend(View view) {
        //RecommendDialog recommend = new RecommendDialog();
        //recommend.show(getSupportFragmentManager(), "Frag");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recommended Item:");

        // Select a random product from that order
        if (!order.isEmpty()){
            int random2 = (int) (Math.random() * 10) % order.size();
            orderItem rec = order.get(random2);

            // Show Dialog Message
            String name = "Name: " + rec.getProductName();
            String price = "Price: $" + String.valueOf(rec.getPrice());
            builder.setMessage(name + "\n" + price);
        }
        else {
            builder.setMessage("Please Make An Order First");
        }
        builder.show();
    }
}