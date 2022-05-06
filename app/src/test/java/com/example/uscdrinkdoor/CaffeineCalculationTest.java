package com.example.uscdrinkdoor;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CaffeineCalculationTest {
    Item item1 = new Item("TestItem", "testDesc", 1, 25, "test1@test.com");
    Item item2 = new Item("TestItem2", "testDesc", 2, 25, "test1@test.com");
    Item item3 = new Item("TestItem3", "testDesc", 3, 15, "test1@test.com");
    Item item4 = new Item("TestItem4", "testDesc", 4, 25, "test1@test.com");
    Item item5 = new Item("TestItem", "testDesc", 5, 10, "test1@test.com");

    ArrayList<Item> itemList = new ArrayList<>();

    ShoppingCart cart1;

//    //Updating order with general info
//    Map<String, Object> pastOrderInfo = new HashMap<>();
//    pastOrderInfo.put("Order Caffeine", orderCaffeine);
//    pastOrderInfo.put("Order Total" , orderTotal);
//    pastOrderInfo.put("Date", timestamp);
//    pastOrderInfo.put("Current", true);

    @Test
    public void calculateCaffeine(){
        int totalCaffeine=0;
        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);
        itemList.add(item4);

        for(Item item: itemList){
            totalCaffeine += item.caffeineMg;
        }

        assertEquals(90, totalCaffeine);

        itemList.remove(1);

        totalCaffeine=0;

        for(Item item: itemList){
            totalCaffeine += item.caffeineMg;
        }

        assertEquals(65, totalCaffeine);

        itemList.add(item5);
        totalCaffeine=0;

        for(Item item: itemList){
            totalCaffeine += item.caffeineMg;
        }


        assertEquals(75, totalCaffeine);

    }


    @Test
    public void testTimeCalculation(){
        long totalCaffeineToday = 0;


        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        //time of first test item
        try {
            date = dateFormat.parse("23/09/2021");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        Long time1 = TimeUnit.MILLISECONDS.toSeconds(date.getTime());

        Map<String, Object> order1 = new HashMap<>();
        order1.put("Item", item1);
        order1.put("Date", time1);


        //time of second test item
        //edge case yesterday
        try {
            date = dateFormat.parse("11/04/2022");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        Long time2 = TimeUnit.MILLISECONDS.toSeconds(date.getTime());

        Map<String, Object> order2 = new HashMap<>();
        order2.put("Item", item2);
        order2.put("Date", time2);


        //time of third test item
        Long time3 = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        Map<String, Object> order3 = new HashMap<>();
        order3.put("Item", item3);
        order3.put("Date", time3);


        //time of fourth test item
        //edge case today
        try {
            date = dateFormat.parse("12/04/2022");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        Long time4 = TimeUnit.MILLISECONDS.toSeconds(date.getTime());

        Map<String, Object> order4 = new HashMap<>();
        order4.put("Item", item4);
        order4.put("Date", time4);


        ArrayList<Map<String, Object>> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);
        orders.add(order3);
        orders.add(order4);

        Long timeNow = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        long timeDiff = 0;
        long currentMinutes = 0;
        long currentHours = 0;

        for(Map<String, Object> order: orders){
            timeDiff = timeNow - (Long)order.get("Date");
            currentMinutes = timeDiff/60;
            currentHours = currentMinutes/60;

            if(currentHours<24){
                Item currItem = (Item)order.get("Item");
                assert currItem != null;
                totalCaffeineToday+= currItem.getCaffeine();
            }


        }

        //only two of the dates are within 24hrs
        //we know which two ->25 + 15 = 40

        assertEquals(40, totalCaffeineToday);



    }

}
