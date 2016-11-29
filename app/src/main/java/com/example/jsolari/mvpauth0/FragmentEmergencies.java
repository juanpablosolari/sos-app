package com.example.jsolari.mvpauth0;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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
    public JSONObject user;

    public FragmentEmergencies() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_emergencies, container, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        prefs = this.getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        try {
            user = new JSONObject(prefs.getString("user", "{}"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        arrayAdapter = new FragmentEmergenciesAdapter(this, datos);
        emergenciesList = (ListView)getView().findViewById(R.id.emergenciesList);
        emergenciesList.setAdapter(arrayAdapter);
        emergenciesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
            final EmergencyItem item = ((EmergencyItem) a.getItemAtPosition(position));
            try {
                if (user != null && user.getBoolean("isVolunteer")) {
                    Dialog dialog = onConfirmDialog(new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        try {
                            answerEmergency(item, user);
                            MainActivity.showMapMarker(item);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.cancel();
                        }
                    });
                    dialog.show();
                } else if (user.getBoolean("isVolunteer")) {
                    notVolunteerDialog().show();
                }
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
        ApiSrv.getEmergencies(new JsonHttpResponseHandler() {
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

    public static void answerEmergency(EmergencyItem item, JSONObject finalUser) throws JSONException {
        RequestParams params = new RequestParams();
        ApiSrv.answerEmergency(item.getId(), finalUser.getString("_id"), new JsonHttpResponseHandler() {
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

    public Dialog onConfirmDialog(DialogInterface.OnClickListener a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Confirmar?")
            .setPositiveButton("Dale", a)
            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Log.i("Dialogos", "Confirmacion Cancelada.");
                    dialog.cancel();
                }
            });

        return builder.create();
    }

    public Dialog notVolunteerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("No sos voluntario")
            .setPositiveButton("Ser voluntario", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            })
            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

        return builder.create();
    }
}
