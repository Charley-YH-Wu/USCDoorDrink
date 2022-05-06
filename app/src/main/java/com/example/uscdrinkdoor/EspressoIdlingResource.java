package com.example.uscdrinkdoor;

import androidx.test.espresso.idling.CountingIdlingResource;

public class EspressoIdlingResource {
    static String resource = "GLOBAL";

    static CountingIdlingResource countingIdlingResource = new CountingIdlingResource(resource);

    public static void increment(){
        countingIdlingResource.increment();
    }

    public static void decrement(){

        if(!countingIdlingResource.isIdleNow()) {
            countingIdlingResource.decrement();
        }
    }
}
