package com.example.jsolari.mvpauth0;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        if (MyFirebaseMessagingService.OK_ACTION.equals(action)) {
            Toast.makeText(context, R.string.si, Toast.LENGTH_SHORT).show();
        }
        else  if (MyFirebaseMessagingService.CANCEL_ACTION.equals(action)) {
            Toast.makeText(context, R.string.no, Toast.LENGTH_SHORT).show();
        }
    }
}
