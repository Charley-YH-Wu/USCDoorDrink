package com.example.uscdrinkdoor;

import java.util.HashMap;
import java.util.Map;

public class User {
    String username;
    String password;

    public String getUsername(){
        return  username;
    };
    public void setUsername(String username){
        this.username = username;
    };

    public String getPassword(){
        return password;
    };
    public void setPassword(String password){
        this.password = password;
    };


    public Map<String, Object> testAccountInfo(Map<String, Object> user){

        String temail = user.get("emailAddress").toString();
        String tname  = user.get("name").toString();
        String taddress  =  user.get("address").toString();
        String tzip  =  user.get("zip").toString();
        String tphone  =  user.get("phone").toString();
        String tbirthday =  user.get("birthday").toString();
        Boolean tstore = (Boolean) user.get("store");
        double tlat  = (Double) user.get("lat");
        double tlongitude  = (Double) user.get("long");
        long tcaffeine  = (Long) user.get("caffeine");

        Map<String, Object> testedUser = new HashMap<>();

        Map<String, Object> tuser = new HashMap<>();
        tuser.put("emailAddress", temail);
        tuser.put("name", tname );
        tuser.put("address", taddress);
        tuser.put("zip", tzip);
        tuser.put("phone", tphone);
        tuser.put("birthday", tbirthday);
        tuser.put("store", tstore);
        tuser.put("lat", tlat);
        tuser.put("long", tlongitude);
        tuser.put("caffeine", tcaffeine);

        return tuser;
    }

}
