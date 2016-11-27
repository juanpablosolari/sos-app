package com.example.jsolari.mvpauth0;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.LockCallback;
import com.auth0.android.lock.utils.CustomField;
import com.auth0.android.lock.utils.LockException;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.example.jsolari.mvpauth0.utils.CredentialsManager;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    public Auth0 auth0;
    private Lock mLock;
    private static ApiSrv ApiSrv = new ApiSrv();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Request a refresh token along with the id token.
        auth0 = new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain));
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("scope", "openid profile user_metadata");

        mLock = Lock.newBuilder(auth0, mCallback)
                .withAuthenticationParameters(parameters)
                //Add parameters to the build
                .build(this);

        if (CredentialsManager.getCredentials(this).getIdToken() == null) {
            startActivity(mLock.newIntent(this));
            return;
        }

        getAuth0Profile();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Your own Activity code
        mLock.onDestroy(this);
        mLock = null;
    }

    private final LockCallback mCallback = new AuthenticationCallback() {
        @Override
        public void onAuthentication(Credentials credentials) {
            Toast.makeText(getApplicationContext(), "Log In - Success", Toast.LENGTH_SHORT).show();
            Log.d("credentials", credentials.toString());
            CredentialsManager.saveCredentials(getApplicationContext(), credentials);
            getAuth0Profile();
            //startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        @Override
        public void onCanceled() {
            Toast.makeText(getApplicationContext(), "Log In - Cancelled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(LockException error) {
            Toast.makeText(getApplicationContext(), "Log In - Error Occurred", Toast.LENGTH_SHORT).show();
        }
    };

    protected void getAuth0Profile() {
        AuthenticationAPIClient aClient = new AuthenticationAPIClient(auth0);
        aClient.tokenInfo(CredentialsManager.getCredentials(this).getIdToken())
                .start(new BaseCallback<UserProfile, AuthenticationException>() {
                    @Override
                    public void onSuccess(final UserProfile payload) {
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Automatic Login Success", Toast.LENGTH_SHORT).show();
                            }
                        });

                        //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        getUserProfile(payload);
                    }

                    @Override
                    public void onFailure(AuthenticationException error) {
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Error al ingresar", Toast.LENGTH_SHORT).show();
                            }
                        });
                        CredentialsManager.deleteCredentials(getApplicationContext());
                        startActivity(mLock.newIntent(LoginActivity.this));
                    }
                });
    }

    public void getUserProfile(UserProfile payload) {
        ApiSrv.upsertUser(payload, new JsonHttpResponseHandler() {
            private Object application;

            public Object getApplication() {
                return application;
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                LoginActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(LoginActivity.this, "Hola!", Toast.LENGTH_SHORT).show();
                    }
                });
                //((Global) this.getApplication()).setUser(responseBody);
                SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user", responseBody.toString());
                editor.commit();

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("LoginActivity", "failure: " + responseString);
                Log.e("LoginActivity", "failurecode: " + statusCode);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Login Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}