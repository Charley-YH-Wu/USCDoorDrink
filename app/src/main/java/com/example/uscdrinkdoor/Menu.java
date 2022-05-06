package com.example.uscdrinkdoor;


import java.util.ArrayList;

public class Menu {
    ArrayList<Item> menu = new ArrayList<Item>();

    public void addItem(Item item){
        menu.add(item);
    }

    public boolean checkItem(Item item){
        for (int i=0; i<menu.size(); i++){
            if (menu.get(i).getName().equals(item.getName())){
                return true;
            }
        }

        return false;
    }

    public void removeItem(Item item){
        menu.remove(item);
    }

    public void editItem(Item item, String name, String desc, long price, long caffeine){
        for (int i=0; i<menu.size(); i++){
            if (menu.get(i).getName().equals(item.getName())){
                menu.get(i).changeName(name);
                menu.get(i).changeDescription(desc);
                menu.get(i).changePrice(price);
                menu.get(i).changeCaffeine(caffeine);
            }
        }
    }

    public int numItems(){
        return menu.size();
    }




}
