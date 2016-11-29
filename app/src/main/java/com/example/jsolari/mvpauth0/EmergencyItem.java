package com.example.jsolari.mvpauth0;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by leman on 11/12/2016.
 */

public class EmergencyItem {
    private String _id;
    private String title;
    private String body;
    private String comuna;
    private String location;
    private String token;

    public EmergencyItem(JSONObject item) throws JSONException {
        _id = item.getString("_id");
        title = item.getString("title");
        body = item.getString("body");
        comuna = item.getString("comuna");
        location = item.getString("location");
        token = item.getString("token");
    }

    public String getId(){ return _id; }
    public String getTitle(){ return title; }

    public String getBody(){
        return body;
    }

    public String getComuna(){
        return comuna;
    }

    public String getLocation(){
        return location;
    }

    public String getToken(){
        return token;
    }
}