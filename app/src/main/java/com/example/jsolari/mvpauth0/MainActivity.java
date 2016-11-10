package com.example.jsolari.mvpauth0;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.auth0.android.Auth0;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.LockCallback;
import com.auth0.android.lock.utils.LockException;
import com.auth0.android.result.Credentials;
import com.auth0.android.lock.AuthenticationCallback;

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
    }

    private LockCallback callback = new AuthenticationCallback() {
        @Override
        public void onAuthentication(Credentials credentials) {
            //Authenticated
        }

        @Override
        public void onCanceled() {
            //User pressed back
        }

        @Override
        public void onError(LockException error) {
            //Exception occurred
        }
        };
    }