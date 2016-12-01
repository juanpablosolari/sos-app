package com.example.jsolari.mvpauth0;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.renderscript.Double2;
import android.util.Log;

import com.auth0.android.result.UserProfile;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.Header;

public class ApiSrv {

    //private static final String BASE_URL = "http://192.168.0.101:3001";
    private static final String BASE_URL = "https://sos-api-qa.herokuapp.com";
    private static final String BASE_URL_PROD = "https://sos-api-prod.herokuapp.com";

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static SyncHttpClient syncClient = new SyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public void upsertUser(UserProfile user, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("email", user.getEmail());
        params.put("name", user.getName());
        params.put("avatar", user.getPictureURL());
        syncClient.post(getAbsoluteUrl("/users"), params, responseHandler);
    }
    public void updateUser(String userId, Boolean wantToBeVolunteer, String comuna, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("isVolunteer", wantToBeVolunteer);
        params.put("comuna", comuna);
        params.put("token", FirebaseInstanceId.getInstance().getToken());
        client.post(getAbsoluteUrl("/users/" + userId), params, responseHandler);
    }

    public void getEmergencies(AsyncHttpResponseHandler responseHandler) {
        syncClient.get(getAbsoluteUrl("/incidents"), responseHandler);
    }

    public void getAsyncEmergencies(AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl("/incidents"), responseHandler);
    }

    public void sendEmergency(Location loc, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("latitude", loc.getLatitude());
        params.put("longitude", loc.getLongitude());
        params.put("token", FirebaseInstanceId.getInstance().getToken());

        client.post(getAbsoluteUrl("/incidents"), params, responseHandler);
    }
    public void answerEmergency(String incidentsId, String userId, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("user", userId);
        params.put("token", FirebaseInstanceId.getInstance().getToken());
        client.post(getAbsoluteUrl("/incidents/" + incidentsId + "/respond"), params, responseHandler);
    }

    public static void getDistanceBetween(LatLng from, LatLng to, AsyncHttpResponseHandler responseHandler){
        Double fromLongitude = from.longitude;
        Double fromLatitude = from.latitude;
        Double toLongitude = to.longitude;
        Double toLatitude = to.latitude;
        client.get("http://maps.googleapis.com/maps/api/distancematrix/json?origins="+fromLatitude.toString()+","+fromLongitude.toString()+"&destinations="+toLatitude.toString()+","+toLongitude.toString()+"&mode=walking&sensor=false", new RequestParams(), responseHandler);
    }
}
