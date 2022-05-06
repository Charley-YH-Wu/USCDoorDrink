package com.example.uscdrinkdoor;

import static org.junit.Assert.assertEquals;



import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import org.junit.runner.RunWith;

public class CreateAccountTest {

    User testAccount = new User();


    @Test
    public void accountIsSavedCorrectly() {


       //test data
        String email = "test@email.com";
        String name = "testName";
        String address = "testAddress";
        String zip = "testZip";
        String phone = "test@email.com";
        String birthday = "testBirthday";
        Boolean store = true;
        double lat = 34.040807199999996;
        double longitude = -118.26463559999999;
        long caffeine = 0;


        Map<String, Object> user = new HashMap<>();
        user.put("emailAddress", email);
        user.put("name", name );
        user.put("address", address);
        user.put("zip", zip);
        user.put("phone", phone);
        user.put("birthday", birthday);
        user.put("store", store);
        user.put("lat", lat);
        user.put("long", longitude);
        user.put("caffeine", caffeine);




        //use the same logic as when passing to DB
        Map<String, Object> tUser = testAccount.testAccountInfo(user);

        assertEquals("Name: ", name, tUser.get("name").toString());
        assertEquals("Email: ", email, tUser.get("emailAddress").toString());
        assertEquals("Address: ", address, tUser.get("address").toString());
        assertEquals("Zip: ", zip, tUser.get("zip").toString());
        assertEquals("Phone: ", phone, tUser.get("phone").toString());
        assertEquals("Bday: ", birthday, tUser.get("birthday").toString());
        assertEquals("Store: ", store, (Boolean) tUser.get("store"));
        assertEquals("Lat: ", lat, (Double)tUser.get("lat"), 0);
        assertEquals("Long: ", longitude, (Double) tUser.get("long"), 0);
        assertEquals("Caffeine: ", caffeine, (Long)tUser.get("caffeine"), 0);

    }

}
