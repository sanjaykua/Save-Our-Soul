package com.hash.sos.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Message {
    public String uid;
    public String mid;
    public long time;
    public double lng;
    public double lat;
    public String msg;
    public String status;

    public Message(){}

    public Message(String mid, String uid, String msg, long time, double lat, double lng) {
        this.mid = mid;
        this.uid = uid;
        this.msg = msg;
        this.time = time;
        this.lat = lat;
        this.lng = lng;
        this.status = "false";
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("mid", mid);
        result.put("uid", uid);
        result.put("msg", msg);
        result.put("time", time);
        result.put("lat", lat);
        result.put("lng", lng);
        result.put("status", status);
        return result;
    }
}
