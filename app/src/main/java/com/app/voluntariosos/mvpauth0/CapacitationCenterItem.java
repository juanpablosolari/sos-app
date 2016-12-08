package com.app.voluntariosos.mvpauth0;


public class CapacitationCenterItem {
    private String title;
    private String body;

    public CapacitationCenterItem(String username, String txt){
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