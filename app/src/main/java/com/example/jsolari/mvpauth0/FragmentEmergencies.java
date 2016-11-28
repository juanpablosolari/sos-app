package com.example.jsolari.mvpauth0;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class FragmentEmergencies extends Fragment {

    public ListView emergenciesList;
    public static FragmentEmergenciesAdapter arrayAdapter;
    private ListView lstOpciones;
    public static SharedPreferences prefs;
    private static ApiSrv ApiSrv = new ApiSrv();
    private ArrayList<EmergencyItem> datos = new ArrayList<EmergencyItem>();

    public FragmentEmergencies() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_emergencies, container, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        prefs = this.getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        arrayAdapter = new FragmentEmergenciesAdapter(this, datos);
        emergenciesList = (ListView)getView().findViewById(R.id.emergenciesList);
        emergenciesList.setAdapter(arrayAdapter);
        emergenciesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                EmergencyItem item = ((EmergencyItem) a.getItemAtPosition(position));
                try {
                    MainActivity.showMapMarker(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            JSONArray items = new JSONArray(prefs.getString("emergencies", "[]"));
            if (items.length() > 0) {
                setEmergencies(items);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("emergencies");
                editor.commit();
            } else {
                getEmergencies();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class FragmentEmergenciesAdapter extends ArrayAdapter<EmergencyItem> {
        Activity context;

        public FragmentEmergenciesAdapter(Fragment context, ArrayList<EmergencyItem> datos) {
            super(context.getActivity(), R.layout.item_emergency, datos);
            this.context = context.getActivity();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View item = inflater.inflate(R.layout.item_emergency, null);

            TextView lblTitulo = (TextView)item.findViewById(R.id.title);
            lblTitulo.setText(datos.get(position).getBody());

            TextView lblSubtitulo = (TextView)item.findViewById(R.id.body);
            lblSubtitulo.setText(datos.get(position).getComuna());

            return(item);
        }
    }
    public static void sendEmergency(Location loc){
        MainActivity.showEmergencyToast("text");

        ApiSrv.sendEmergency(loc, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                if (responseBody != null) {
                    Log.d("Emergencies", responseBody.toString());
                }
                getEmergencies();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("Emergencies",  "failure: " + responseString);
                Log.e("Emergencies",  "failurecode: " + statusCode);
            }
        });
    }

    public static void getEmergencies(){
        RequestParams params = new RequestParams();

        ApiSrv.get("/emergencies", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                if (prefs != null) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("emergencies", responseBody.toString());
                    editor.commit();
                }
                setEmergencies(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("Emergencies",  "failure: " + responseString);
                Log.e("Emergencies",  "failurecode: " + statusCode);
            }
        });
    }

    public static void setEmergencies(JSONArray items){
        if (arrayAdapter != null) {
            arrayAdapter.clear();
        }

        for (int i = 0; i < items.length(); i++ ) {
            try {
                JSONObject item = items.getJSONObject(i);
                if (arrayAdapter != null) {
                    arrayAdapter.add(new EmergencyItem(item));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void UpdateEmergencies(JSONObject item) throws JSONException {
        arrayAdapter.add(new EmergencyItem(item));
    }
}
