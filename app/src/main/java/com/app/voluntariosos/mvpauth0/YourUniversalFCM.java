package com.app.voluntariosos.mvpauth0;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by leman on 12/29/2016.
 */

public class YourUniversalFCM extends WakefulBroadcastReceiver {

    private static final String ACTION_REGISTRATION = "com.google.android.c2dm.intent.REGISTRATION";
    private static final String ACTION_RECEIVE = "com.google.android.c2dm.intent.RECEIVE";
    private static final String OK_ACTION = "com.google.android.c2dm.intent.OK_ACTION";
    private static final String CANCEL_ACTION = "com.google.android.c2dm.intent.CANCEL_ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        for (String key : intent.getExtras().keySet()) {
            Log.d("TAG",  "{UniversalFCM}->onReceive: key->" + key + ", value->" + intent.getExtras().get(key));
        }

        Bundle a = intent.getExtras();
        String title = a.getString("gcm.notification.title");
        String body = a.getString("gcm.notification.body");

        JSONObject incident = new JSONObject();
        try {
            incident.put("_id", a.getString("_id"));
            incident.put("body", a.getString("body"));
            incident.put("from", a.getString("from"));
            incident.put("type", a.getString("type"));
            incident.put("title", a.getString("title"));
            incident.put("token", a.getString("token"));
            incident.put("location", a.getString("location"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Set sound of notification
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent _intent = new Intent(context, MainActivity.class);
        _intent.setAction(this.OK_ACTION);
        Bundle c = new Bundle();
        c.putString("notification", incident.toString());
        _intent.putExtras(c);
        _intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Intent _intent_cancel = new Intent(context, MainActivity.class);
        _intent_cancel.setAction(this.CANCEL_ACTION);
        _intent_cancel.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, _intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent cancelIntent = PendingIntent.getActivity(context, 0, _intent_cancel, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setOngoing(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent);

        try {
            if (incident.has("type")) {
                String type = incident.getString("type");
                if (type.equals("incident")) {
                    notifiBuilder.addAction(R.drawable.back_dialog, "Ayudo", pendingIntent);
                    notifiBuilder.addAction(R.drawable.back_dialog, "No Ayudo", cancelIntent);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notifiBuilder.build());

        abortBroadcast();
    }
}

