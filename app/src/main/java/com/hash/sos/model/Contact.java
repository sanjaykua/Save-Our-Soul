package com.hash.sos.model;


import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Contact {
    public String uid;
    public String cid;
    public String name;
    public String number;

    public Contact(){}

    public Contact(String cid, String name, String number) {
        this.cid = cid;
        this.name = name;
        this.number = number;
    }

    public Contact(String cid, String uid, String name, String number) {
        this.cid = cid;
        this.uid = uid;
        this.name = name;
        this.number = number;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("cid", cid);
        result.put("uid", uid);
        result.put("name", name);
        result.put("number", number);
        return result;
    }
}