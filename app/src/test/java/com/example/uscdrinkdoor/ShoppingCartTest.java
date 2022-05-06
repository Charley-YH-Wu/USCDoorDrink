package com.example.uscdrinkdoor;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;




@RunWith(JUnit4.class)
public class ShoppingCartTest {

    @Test
    public void CalculateTotalTest() {
        List<Item> l = new ArrayList<>();

        l.add(new Item("a", "",10,5,""));
        l.add(new Item("b", "",8,5,""));
        ShoppingCart cart1 = new ShoppingCart(l);
        assertEquals(18, (int)cart1.calculateTotal());

        l.add(new Item("c", "",5,5,""));
        l.add(new Item("d", "",7,5,""));
        ShoppingCart cart2 = new ShoppingCart(l);
        assertEquals(30, (int)cart2.calculateTotal());

        l.add(new Item("d", "",6,5,""));
        ShoppingCart cart3 = new ShoppingCart(l);
        assertEquals(36, (int)cart3.calculateTotal());
    }
}