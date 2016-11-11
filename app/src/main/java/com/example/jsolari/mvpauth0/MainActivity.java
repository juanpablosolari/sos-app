package com.example.jsolari.mvpauth0;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.LockCallback;
import com.auth0.android.lock.utils.LockException;
import com.auth0.android.result.Credentials;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.result.UserProfile;
import com.example.jsolari.mvpauth0.utils.CredentialsManager;

public class MainActivity extends AppCompatActivity {
    private Lock lock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Auth0 auth0 = new Auth0("QHgkaDd0lh7IDr3NXup3AnegmmMbqZe8","juanpablosolari.auth0.com");

        lock = Lock.newBuilder(auth0, callback)
                .build(this);

        startActivity(lock.newIntent(this));

        if (CredentialsManager.getCredentials(this).getIdToken() == null) {
            startActivity(lock.newIntent(this));
            return;
        }

        AuthenticationAPIClient aClient = new AuthenticationAPIClient(auth0);
        aClient.tokenInfo(CredentialsManager.getCredentials(this).getIdToken())
                .start(new BaseCallback<UserProfile, AuthenticationException>() {
                    @Override
                    public void onSuccess(final UserProfile payload) {
                        //LoginActivity.this.runOnUiThread(new Runnable() {
                           MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                //Toast.makeText(LoginActivity.this, "Automatic Login Success", Toast.LENGTH_SHORT).show();
                                Toast.makeText(MainActivity.this, "Automatic Login Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(AuthenticationException error) {
                        //LoginActivity.this.runOnUiThread(new Runnable() {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                //Toast.makeText(LoginActivity.this, "Session Expired, please Log In", Toast.LENGTH_SHORT).show();
                                Toast.makeText(MainActivity.this, "Session Expired, please Log In", Toast.LENGTH_SHORT).show();
                            }
                        });
                        CredentialsManager.deleteCredentials(getApplicationContext());
                        //startActivity(mLock.newIntent(LoginActivity.this));
                        startActivity(lock.newIntent(MainActivity.this));
                    }
                });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Your own Activity code
        lock.onDestroy(this);
        lock = null;
    }

    private LockCallback callback = new AuthenticationCallback() {
        @Override
        public void onAuthentication(Credentials credentials) {
            //Authenticated
            CredentialsManager.saveCredentials(getApplicationContext(), credentials);

            Toast.makeText(getApplicationContext(), "Log In - Success", Toast.LENGTH_SHORT).show();
            CredentialsManager.saveCredentials(getApplicationContext(), credentials);
            //startActivity(new Intent(LoginActivity.this, MainActivity.class));
            startActivity(new Intent(MainActivity.this, MainActivity.class));
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
    }