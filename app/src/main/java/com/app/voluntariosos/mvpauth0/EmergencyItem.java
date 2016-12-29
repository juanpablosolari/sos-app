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
    private String type;

    public EmergencyItem(JSONObject item) throws JSONException {
        if (item.has("_id")) {
            _id = item.getString("_id");
        }
        if (item.has("title")) {
            title = item.getString("title");
        }
        if (item.has("body")) {
            body = item.getString("body");
        }
        if (item.has("comuna")) {
            comuna = item.getString("comuna");
        }
        if (item.has("location")) {
            location = item.getString("location");
        }
        if (item.has("token")) {
            token = item.getString("token");
        }
        if (item.has("horario")) {
            horario = item.getString("horario");
        }
        if (item.has("type")) {
            type = item.getString("type");
        }
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

    public String getType(){
        return type;
    }
}