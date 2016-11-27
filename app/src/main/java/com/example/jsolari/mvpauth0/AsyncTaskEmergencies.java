package com.example.jsolari.mvpauth0;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by leman on 11/27/2016.
 */

public class AsyncTaskEmergencies extends AsyncTask {

    private static ApiSrv ApiSrv = new ApiSrv();

    @Override
    protected Boolean doInBackground(final Object[] params) {
        try{
            ApiSrv.getEmergencies(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray responseBody) {
                    super.onSuccess(statusCode, headers, responseBody);
                    Context context = (Context) params[0];
                    if (responseBody.length() > 0) {
                        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("emergencies", responseBody.toString());
                        editor.commit();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.e("doInBackground", "failure: " + responseString);
                    Log.e("doInBackground", "failurecode: " + statusCode);
                }
            });
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
