package com.example.uscdrinkdoor;


import static org.junit.Assert.*;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;



@RunWith(JUnit4.class)
public class MapsActivityTest {

    String clicked = null;
    String traveloption = null;

    public void Marker_click(String s) { // first click, single
        if (clicked == null) {
            clicked = s;
        }
        else if (clicked.equals(s)) { // double click
            clicked = null;
        }
        else { // single click
            clicked = s;
        }
    }

    public void SelectWalking() { traveloption = "walking"; }
    public void SelectDriving(){ traveloption = "driving"; }

    public String GetURL(LatLng l, String t){
        String origin = "origin=" + "34.0213" + "," + "-118.2824";
        String dest = "&destination=" + l.latitude + "," + l.longitude;
        String mode = "&mode=" + t;
        String key = "&key=AIzaSyDewc_xqcDgxGJNJAEb0D3ipsKtxD3KqOI";
        String urlrequest = "https://maps.googleapis.com/maps/api/directions/json?" + origin + dest + mode + key;
        return urlrequest;
    }

    @Test
    public void Test_On_Marker_Click_Logic(){

        // Simulate markers
        String a = "a";
        String b = "b";
        String c = "c";

        // Simulate single click on stores
        Marker_click(a);
        assertEquals(clicked, a);
        Marker_click(b);
        assertEquals(clicked, b);
        Marker_click(c);
        assertEquals(clicked, c);

        // Simulate double click on stores
        Marker_click(a);
        Marker_click(a);
        assertEquals(clicked, null);
        Marker_click(b);
        Marker_click(b);
        assertEquals(clicked, null);
        Marker_click(c);
        Marker_click(c);
        assertEquals(clicked, null);
    }

    @Test
    public void Test_Select_Different_Travel_Options(){
        SelectWalking();
        assertEquals(traveloption, "walking");
        SelectWalking();
        assertEquals(traveloption, "walking");
        SelectDriving();
        assertEquals(traveloption, "driving");
        SelectDriving();
        assertEquals(traveloption, "driving");
    }

    @Test
    public void Address_To_Coordinates(){
        LatLng l = new LatLng(34.029496,-118.285165);
        String c = GetURL(l, "walking");
        assertEquals("https://maps.googleapis.com/maps/api/" +
                "directions/json?origin=34.0213,-118.2824&destination=34.029496,-118.285165&mode=" +
                "walking&key=AIzaSyDewc_xqcDgxGJNJAEb0D3ipsKtxD3KqOI", c );
        c = GetURL(l, "driving");
        assertEquals("https://maps.googleapis.com/maps/api/" +
                "directions/json?origin=34.0213,-118.2824&destination=34.029496,-118.285165&mode=driving" +
                "&key=AIzaSyDewc_xqcDgxGJNJAEb0D3ipsKtxD3KqOI", c);

        LatLng l2 = new LatLng(34.0223,-118.2846);
        c = GetURL(l2, "walking");
        assertEquals("https://maps.googleapis.com/maps/api/" +
                "directions/json?origin=34.0213,-118.2824&destination=34.0223,-118.2846&mode=walking" +
                "&key=AIzaSyDewc_xqcDgxGJNJAEb0D3ipsKtxD3KqOI", c);

        c = GetURL(l2, "driving");
        assertEquals("https://maps.googleapis.com/maps/api/" +
                "directions/json?origin=34.0213,-118.2824&destination=34.0223,-118.2846&mode=driving" +
                "&key=AIzaSyDewc_xqcDgxGJNJAEb0D3ipsKtxD3KqOI", c);
    }
}