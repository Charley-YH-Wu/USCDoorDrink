package com.example.uscdrinkdoor;

import static org.junit.Assert.assertEquals;



import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import org.junit.runner.RunWith;

public class CartTest {
    Cart cartTest = new Cart();

    @Test
    public void checkAddToCart(){
        Item i1 = new Item("Coffee", "Hot", 5, 50, "a@a.com");
        Item i2 = new Item("Tea", "Cold", 5, 20, "b@a.com");
        Item i3 = new Item("Boba", "Cold", 5, 10, "c@a.com");

        cartTest.addItem(i1);
        cartTest.addItem(i2);
        cartTest.addItem(i3);

        assertEquals(true, cartTest.checkItem(i1));
        assertEquals(true, cartTest.checkItem(i2));
        assertEquals(true, cartTest.checkItem(i3));

    }

    @Test
    public void checkRemoveFromCart(){
        Item i1 = new Item("Coffee", "Hot", 5, 50, "a@a.com");
        Item i2 = new Item("Tea", "Cold", 5, 20, "b@a.com");
        Item i3 = new Item("Boba", "Cold", 5, 10, "c@a.com");

        cartTest.addItem(i1);
        cartTest.addItem(i2);
        cartTest.addItem(i3);

        assertEquals(true, cartTest.checkItem(i1));
        assertEquals(true, cartTest.checkItem(i2));
        assertEquals(true, cartTest.checkItem(i3));

        cartTest.removeItem(i3);

        assertEquals(true, cartTest.checkItem(i1));
        assertEquals(true, cartTest.checkItem(i2));
        assertEquals(false, cartTest.checkItem(i3));


    }
}