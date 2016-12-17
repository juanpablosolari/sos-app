package com.app.voluntariosos.mvpauth0;


import org.json.JSONException;
import org.json.JSONObject;

public class CapacitationCenterItem {
    private String id;
    private String title;
    private String body;

    public CapacitationCenterItem(JSONObject item) throws JSONException {
        id = item.getString("_id");
        title = item.getString("name");
        body = item.getString("description");
    }

    public String getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getBody(){
        return body;
    }
}