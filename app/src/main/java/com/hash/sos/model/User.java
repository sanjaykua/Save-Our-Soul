package com.hash.sos.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {
    public String uid;
    public String number;
    public String email;
    public String name;
    public String message;
    public String address;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uId,String number) {
        this.uid = uId;
        this.number = number;
    }

    public User(String uId,String number,String email, String name, String message, String address) {
        this.uid = uId;
        this.number = number;
        this.name = name;
        this.email = email;
        this.message = message;
        this.address = address;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("number", number);
        result.put("email", email);
        result.put("name", name);
        result.put("message", message);
        result.put("address", address);
        return result;
    }

}
// [END blog_user_class]
