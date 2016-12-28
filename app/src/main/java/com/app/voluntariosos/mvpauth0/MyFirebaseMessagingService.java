package com.app.voluntariosos.mvpauth0;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "123";
    public static final String OK_ACTION = "OK_ACTION";
    public static final String CANCEL_ACTION = "CANCEL_ACTION";

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "FROM:" + remoteMessage.getFrom());

        //Check if the message contains data
        if(remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data: " + remoteMessage.getData());
        }

        //Check if the message contains notification

        if(remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Mesage body:" + body);
            sendNotification(title, body, remoteMessage.getData().toString());
        }
    }

    public PendingIntent getPendingAction(Context context) {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("CLICK", true);
        Log.e(TAG, "set action : ");

        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    /**
     * Dispay the notification
     * @param title
     * @param body
     */
    private void sendNotification(final String title, final String body, final String Data) {

        //Set sound of notification
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(this.OK_ACTION);
        Bundle c = new Bundle();
        c.putString("notification", Data);
        intent.putExtras(c);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Intent intent_cancel = new Intent(this, MainActivity.class);
        intent_cancel.setAction(this.CANCEL_ACTION);
        intent_cancel.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent cancelIntent = PendingIntent.getActivity(this, 0, intent_cancel, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(notificationSound);

        JSONObject noti = null;
        try {
            noti = new JSONObject(c.getString("notification"));
            if (noti.has("type")) {
                String type = noti.getString("type");
                if (type.equals("incident")) {
                    notifiBuilder.addAction(R.drawable.back_dialog, "Ayudo", pendingIntent);
                    notifiBuilder.addAction(R.drawable.back_dialog, "No Ayudo", cancelIntent);
                    notifiBuilder.setContentIntent(pendingIntent);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notifiBuilder.build());
    }
}
