package com.app.voluntariosos.mvpauth0;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.Timer;
import java.util.TimerTask;

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

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Cargando Emergencias...");
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

            TextView lblHorario  =(TextView)item.findViewById(R.id.horario);
            lblHorario.setText(datos.get(position).getHorario());
            return(item);
        }
    }
    public static void sendEmergency(Location loc, JSONObject finalUser) throws JSONException {
        MainActivity.showEmergencyToast("text");

        ApiSrv.sendEmergency(loc, finalUser.getString("_id"), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                if (responseBody != null) {
                    Log.d(String.valueOf(R.string.Emergencies), responseBody.toString());
                }
                getEmergencies();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(String.valueOf(R.string.Emergencies),  "failure: " + responseString);
                Log.e(String.valueOf(R.string.Emergencies),  "failurecode: " + statusCode);
            }
        });
    }

    public static void getEmergencies(){
        ApiSrv.getAsyncEmergencies(new JsonHttpResponseHandler() {
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
                Log.e(String.valueOf(R.string.Emergencies),  "failure: " + responseString);
                Log.e(String.valueOf(R.string.Emergencies),  "failurecode: " + statusCode);
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
                    Log.d(String.valueOf(R.string.Emergencies), responseBody.toString());
                }
                getEmergencies();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(String.valueOf(R.string.Emergencies),  "failure: " + responseString);
                Log.e(String.valueOf(R.string.Emergencies),  "failurecode: " + statusCode);
            }
        });
    }

    public static void setEmergencies(JSONArray items){
        String myToken = FirebaseInstanceId.getInstance().getToken();
        if (arrayAdapter != null) {
            arrayAdapter.clear();
        }

        for (int i = 0; i < items.length(); i++ ) {
            try {
                JSONObject item = items.getJSONObject(i);
                if (arrayAdapter != null && item.getString("token").equals(myToken) == false) {
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

    public Dialog onConfirmDialog(DialogInterface.OnClickListener cb) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.confirm)
            .setPositiveButton(R.string.si, cb)
            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Log.i(getString(R.string.dialogos), getString(R.string.confirmCanceled));
                    dialog.cancel();
                }
            });

        return builder.create();
    }

    public Dialog notVolunteerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.volno)
            .setPositiveButton(R.string.volquiero, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            })
            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

        return builder.create();
    }
}
