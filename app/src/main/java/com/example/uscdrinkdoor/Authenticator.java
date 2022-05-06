package com.example.uscdrinkdoor;

public class Authenticator {
    String username;
    String password;
    String name;

    Authenticator(String username, String password, String name){
        this.username = username;
        this.name = name;
        this.password = password;
    }

    public boolean userAuth(){
        //find user using username
        //hash password and compare to hashed
        //password saved in db

        //return true if equal
        //return false if not

        return false;
    }

    public void createUser(){
        //if username doesnt exist
        //make new entry with username
        // and hashed password
    }
}
