package com.app.voluntariosos.mvpauth0;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class FragmentCapacitationCenter extends Fragment {
    public static SharedPreferences prefs;
    private static ApiSrv ApiSrv = new ApiSrv();
    public TextView name = null;
    public TextView address = null;
    public TextView hours = null;
    public TextView phone = null;
    public TextView email = null;
    public TextView description = null;
    public TextView url = null;

    public FragmentCapacitationCenter() {}

    public static FragmentCapacitationCenter newInstance(String param1, String param2) {
        FragmentCapacitationCenter fragment = new FragmentCapacitationCenter();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_capacitation_center, container, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        prefs = this.getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        name = (TextView) getView().findViewById(R.id.name);
        description = (TextView) getView().findViewById(R.id.description);
        url = (TextView) getView().findViewById(R.id.url);
        address = (TextView) getView().findViewById(R.id.address);
        hours = (TextView) getView().findViewById(R.id.hours);
        phone = (TextView) getView().findViewById(R.id.phone);
        email = (TextView) getView().findViewById(R.id.email);
        phone.setVisibility(View.GONE);

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Cargando datos del centro...");
        dialog.setMessage("Aguarde unos segundos...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        long delayInMillis = 1500;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, delayInMillis);

        String capacitationCenterId = prefs.getString("capacitationCenterId", "");

        final Button btnCall = (Button) getView().findViewById(R.id.btnCall);
        btnCall.setVisibility(View.GONE);
        btnCall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String number = (String) phone.getText();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" +number));
                startActivity(intent);
            }
        });

        ApiSrv.getCapacitationCenter(capacitationCenterId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                Log.d("Centers", responseBody.toString());

                try {
                    name.setText(responseBody.getString("name"));
                    description.setText(responseBody.getString("description"));
                    url.setText(responseBody.getString("url"));
                    JSONObject contact = responseBody.getJSONObject("contact");
                    if (responseBody.has("hours")) {
                        hours.setText(responseBody.getString("hours"));
                    }
                    if (contact.has("email")) {
                        email.setText(contact.getString("email"));
                    }
                    if (contact.has("phone")) {
                        phone.setText(contact.getString("phone"));
                        phone.setVisibility(View.VISIBLE);
                        btnCall.setVisibility(View.VISIBLE);
                    }
                    if (responseBody.has("location")) {
                        JSONObject location = responseBody.getJSONObject("location");
                        JSONObject locAddress = location.getJSONObject("address");
                        String formattedAddress = locAddress.getString("formatted_address");
                        //String formattedAddress = location.getString("address");
                        address.setText(formattedAddress);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("Centers",  "failure: " + responseString);
                Log.e("Centers",  "failurecode: " + statusCode);
            }
        });
    }
}
