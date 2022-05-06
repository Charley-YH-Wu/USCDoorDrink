package com.example.uscdrinkdoor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;

public class SellerWalletTest {


    Item item1 = new Item("TestItem", "testDesc", 1, 25, "test1@test.com");
    Item item2 = new Item("TestItem2", "testDesc", 2, 25, "test1@test.com");
    Item item3 = new Item("TestItem3", "testDesc", 3, 25, "test1@test.com");
    Item item4 = new Item("TestItem4", "testDesc", 4, 25, "test1@test.com");
    Item item5 = new Item("TestItem", "testDesc", 5, 25, "test1@test.com");

    ArrayList<Item> itemList = new ArrayList<>();

    ShoppingCart cart1;
    ShoppingCart cart2;
    ShoppingCart cart3;

    //test logic of finding a cart's total value
    @Test
    public void testCartTotal(){
        itemList.clear();

        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);
        itemList.add(item4);
        itemList.add(item5);


        cart1 = new ShoppingCart(itemList);

        assertEquals(15, cart1.calculateTotal(), 0);
    }

    //test logic behind calculating seller wallet value
    @Test
    public void testManyCarts(){
        float sellerWallet =0;

        itemList.clear();

        itemList.add(item1);
        itemList.add(item2);

        cart1 = new ShoppingCart(itemList);
        sellerWallet += cart1.calculateTotal();

        assertEquals(3, cart1.calculateTotal(), 0);

        itemList.clear();
        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);
        itemList.add(item4);

        cart2 = new ShoppingCart(itemList);

        sellerWallet += cart2.calculateTotal();

        assertEquals(10, cart2.calculateTotal(), 0);



        itemList.clear();
        itemList.add(item1);
        itemList.add(item3);
        itemList.add(item4);

        cart3 = new ShoppingCart(itemList);
        sellerWallet += cart3.calculateTotal();

        assertEquals(8, cart3.calculateTotal(), 0);

        //final check
        assertEquals(21, sellerWallet, 0);


    }


}
