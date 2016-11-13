package com.example.jsolari.mvpauth0;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.content.ContentValues.TAG;

/**
 * Created by nahuel ultrabook on 11/1/2016.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    public void onTokenRefresh() {
        //Get updated token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "New Token1: " + refreshedToken);

        //You can save the token into third party server to do anything you want
    }

    private void sendRegistrationToServer(String refreshedToken) {

    }
}
