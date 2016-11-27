package com.example.jsolari.mvpauth0;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by leman on 11/12/2016.
 */

public class EmergencyItem {
    private String title;
    private String body;
    private String comuna;
    private String location;

    public EmergencyItem(JSONObject item) throws JSONException {
        title = item.getString("title");
        body = item.getString("body");
        comuna = item.getString("comuna");
        location = item.getString("location");
    }

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
}