package com.example.jsolari.mvpauth0;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by leman on 11/13/2016.
 */

public class ApiSrv {


    
    // private static final String BASE_URL = "https://192.168.1.122:3001";
    private static final String BASE_URL = "https://sos-api-qa.herokuapp.com";
    private static final String BASE_URL_PROD = "https://sos-api-prod.herokuapp.com";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

//    SSLContext context = SSLContext.getInstance("SSL")
//    context.init(null, new TrustManager[] {
//        new X509TrustManager {
//            void checkClientTrusted(X509Certificate[] chain, String authType) {}
//        void checkServerTrusted(X509Certificate[] chain, String authType) {}
//        void getAcceptedIssuers() { return null; }
//        }
//    }, null);
}
