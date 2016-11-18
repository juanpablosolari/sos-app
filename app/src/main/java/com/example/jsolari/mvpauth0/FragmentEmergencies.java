package com.example.jsolari.mvpauth0;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class FragmentEmergencies extends Fragment {

    public ListView emergenciesList;
    public static FragmentEmergenciesAdapter arrayAdapter;
    private ListView lstOpciones;

    private static ApiSrv client = new ApiSrv();

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
        arrayAdapter = new FragmentEmergenciesAdapter(this, datos);
        emergenciesList = (ListView)getView().findViewById(R.id.emergenciesList);
        emergenciesList.setAdapter(arrayAdapter);

        getEmergencies();
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
            lblTitulo.setText(datos.get(position).getTitle());

            TextView lblSubtitulo = (TextView)item.findViewById(R.id.body);
            lblSubtitulo.setText(datos.get(position).getBody());

            return(item);
        }
    }
    public static void sendEmergency(Location location){
        RequestParams params = new RequestParams();
        params.put("location", location.toString());
        params.put("token", FirebaseInstanceId.getInstance().getToken());
        
        MainActivity.showEmergencyToast("text");

        client.post("/emergencies", params,  new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                Log.d("Emergencies", responseBody.toString());
                getEmergencies();
                //Toast.makeText(FragmentEmergencies.this, "asd", Toast.LENGTH_LONG).show();
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
        client.get("/emergencies", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                Log.d("Emergencies", responseBody.toString());

                if (arrayAdapter != null) {
                    arrayAdapter.clear();
                }

                for (int i = 0; i < responseBody.length(); i++ ) {
                    String title = null;
                    String body = null;
                    try {
                        JSONObject item = responseBody.getJSONObject(i);
                        title = item.getString("title");
                        body = item.getString("body");
                        if (arrayAdapter != null) {
                            arrayAdapter.add(new EmergencyItem(title, body));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("Emergencies",  "failure: " + responseString);
                Log.e("Emergencies",  "failurecode: " + statusCode);
            }
        });
    }

    public static void UpdateEmergencies(String title, String body){
        arrayAdapter.add(new EmergencyItem(title, body));
    }
}
