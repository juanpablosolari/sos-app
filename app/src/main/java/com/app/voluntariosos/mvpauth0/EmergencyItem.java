package com.app.voluntariosos.mvpauth0;

import org.json.JSONException;
import org.json.JSONObject;


public class EmergencyItem {
    private String _id;
    private String title;
    private String body;
    private String comuna;
    private String location;
    private String token;
    private String horario;

    public EmergencyItem(JSONObject item) throws JSONException {
        _id = item.getString("_id");
        title = item.getString("title");
        body = item.getString("body");
        comuna = item.getString("comuna");
        location = item.getString("location");
        token = item.getString("token");
        horario = item.getString("createdAt");
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

    public String getHorario(){
        return horario;
    }

    public String getToken(){
        return token;
    }
}