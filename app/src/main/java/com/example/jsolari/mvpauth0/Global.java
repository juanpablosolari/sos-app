package com.example.jsolari.mvpauth0;

import android.app.Application;

import org.json.JSONObject;

/**
 * Created by leman on 11/26/2016.
 */

public class Global extends Application {
    private static JSONObject user;

    public Global(JSONObject user) {
        Global.user = user;
    }

    public void setUser(JSONObject user) {
        Global.user = user;
    }

    public static JSONObject getUser() {
        return user;
    }
}
