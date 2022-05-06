package com.example.uscdrinkdoor;

import static org.junit.Assert.assertEquals;



import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import org.junit.runner.RunWith;


public class MenuTest {
    Menu menuTest = new Menu();

    @Test
    public void checkAddToMenu(){
        Item i1 = new Item("Coffee", "Hot", 5, 50, "a@a.com");
        Item i2 = new Item("Tea", "Cold", 5, 20, "b@a.com");
        Item i3 = new Item("Boba", "Cold", 5, 10, "c@a.com");

        menuTest.addItem(i1);
        menuTest.addItem(i2);
        menuTest.addItem(i3);

        assertEquals(true, menuTest.checkItem(i1));
        assertEquals(true, menuTest.checkItem(i2));
        assertEquals(true, menuTest.checkItem(i3));

    }

    @Test
    public void checkEditMenuItem(){
        Item i1 = new Item("Coffee", "Hot", 5, 50, "a@a.com");

        menuTest.addItem(i1);

        assertEquals(true, menuTest.checkItem(i1));

        menuTest.editItem(i1,"Mango Tea", "Yum", 5, 10);

        Item i2 = new Item("Mango Tea", "Yum", 5, 10, "a@a.com");

        assertEquals(true, menuTest.checkItem(i2));

    }

    @Test
    public void checkMenuSize(){
        Item i1 = new Item("Coffee", "Hot", 5, 50, "a@a.com");
        menuTest.addItem(i1);

        assertEquals(1, menuTest.numItems());

        Item i2 = new Item("Tea", "Cold", 5, 20, "b@a.com");
        menuTest.addItem(i2);

        assertEquals(2, menuTest.numItems());


    }

    @Test
    public void checkRemoveFromMenu(){
        Item i1 = new Item("Coffee", "Hot", 5, 50, "a@a.com");
        Item i2 = new Item("Tea", "Cold", 5, 20, "b@a.com");
        Item i3 = new Item("Boba", "Cold", 5, 10, "c@a.com");

        menuTest.addItem(i1);
        menuTest.addItem(i2);
        menuTest.addItem(i3);

        assertEquals(true, menuTest.checkItem(i1));
        assertEquals(true, menuTest.checkItem(i2));
        assertEquals(true, menuTest.checkItem(i3));

        menuTest.removeItem(i3);

        assertEquals(true, menuTest.checkItem(i1));
        assertEquals(true, menuTest.checkItem(i2));
        assertEquals(false, menuTest.checkItem(i3));


    }



}