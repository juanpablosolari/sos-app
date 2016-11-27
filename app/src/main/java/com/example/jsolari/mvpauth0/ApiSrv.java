package com.example.jsolari.mvpauth0;

import android.util.Log;

import com.auth0.android.result.UserProfile;
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

/**
 * Created by leman on 11/13/2016.
 */

public class ApiSrv {

    private static final String BASE_URL = "http://192.168.0.101:3001";
    //private static final String BASE_URL = "https://sos-api-qa.herokuapp.com";
    private static final String BASE_URL_PROD = "https://sos-api-prod.herokuapp.com";

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static SyncHttpClient clientSync = new SyncHttpClient();

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
        clientSync.post(getAbsoluteUrl("/users"), params, responseHandler);
    }
    public void updateUser(String userId, Boolean wantToBeVolunteer, String comuna, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("isVolunteer", wantToBeVolunteer);
        params.put("comuna", comuna);
        client.post(getAbsoluteUrl("/users/" + userId), params, responseHandler);
    }
}
