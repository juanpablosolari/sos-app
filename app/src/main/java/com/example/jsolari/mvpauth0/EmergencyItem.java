package com.example.jsolari.mvpauth0;

/**
 * Created by leman on 11/12/2016.
 */

public class EmergencyItem {
    private String title;
    private String body;

    public EmergencyItem(String username, String txt){
        title = username;
        body = txt;
    }

    public String getTitle(){
        return title;
    }

    public String getBody(){
        return body;
    }
}