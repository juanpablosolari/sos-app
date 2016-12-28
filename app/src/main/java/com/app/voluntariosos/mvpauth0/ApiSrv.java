package com.app.voluntariosos.mvpauth0;

import android.location.Location;

import com.auth0.android.result.UserProfile;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

public class ApiSrv {

    //private static final String BASE_URL = "http://192.168.1.122:3001";
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
        params.put("token", FirebaseInstanceId.getInstance().getToken());
        syncClient.post(getAbsoluteUrl("/users"), params, responseHandler);
    }
    public void updateUser(String userId, Boolean wantToBeVolunteer, String comuna, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("wantToBeVolunteer", wantToBeVolunteer);
        params.put("comuna", comuna);
        params.put("token", FirebaseInstanceId.getInstance().getToken());
        client.post(getAbsoluteUrl("/users/" + userId), params, responseHandler);
    }
    public void updateUserProfile(String userId, JSONObject user, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("user", user.toString());
        params.put("token", FirebaseInstanceId.getInstance().getToken());
        client.post(getAbsoluteUrl("/users/" + userId), params, responseHandler);
    }

    public void getEmergencies(AsyncHttpResponseHandler responseHandler) {
        syncClient.get(getAbsoluteUrl("/incidents"), responseHandler);
    }

    public void getAsyncEmergencies(AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl("/incidents"), responseHandler);
    }

    public void sendEmergency(Location loc, String userId, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("latitude", loc.getLatitude());
        params.put("longitude", loc.getLongitude());
        params.put("token", FirebaseInstanceId.getInstance().getToken());
        params.put("user", userId);
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

    public void getCapacitationCenters(AsyncHttpResponseHandler responseHandler){
        client.get(getAbsoluteUrl("/capacitation-centers"), new RequestParams(), responseHandler);
    }

    public void getCapacitationCenter(String id, AsyncHttpResponseHandler responseHandler){
        client.get(getAbsoluteUrl("/capacitation-centers/" + id), new RequestParams(), responseHandler);
    }
}
