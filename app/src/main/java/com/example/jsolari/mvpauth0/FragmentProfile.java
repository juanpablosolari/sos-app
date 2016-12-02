package com.example.jsolari.mvpauth0;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class FragmentProfile extends Fragment {

    private Spinner comunaSpinner;
    private ArrayAdapter<String> comunaAdapter;
    private Context applicationContext;
    private CheckBox wantToBeVolunteer;
    private String comuna;
    private static ApiSrv ApiSrv = new ApiSrv();
    private SharedPreferences prefs;
    public JSONObject userJson = null;
    public Boolean isUserVolunteer = false;
    public String userComuna = "";
    public TextView MyComuna;

    public FragmentProfile() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);


    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        prefs = this.getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String user = prefs.getString("user", "");
        wantToBeVolunteer = (CheckBox) getView().findViewById(R.id.wantToBeVolunteer);

        MyComuna = (TextView) getView().findViewById(R.id.MyComuna);
        MyComuna.setText("Actualmente no pertenece a ninguna comuna.");
        try {
            userJson = new JSONObject(user);
            isUserVolunteer = userJson.getBoolean("isVolunteer");
            userComuna = userJson.getString("comuna");
            MyComuna.setText("Actualmente usted pertenece a la " + userComuna);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isUserVolunteer.equals(true)) {
            wantToBeVolunteer.setChecked(true);
        }

        comunaSpinner = (Spinner) getView().findViewById(R.id.comunaSpinner);
        ArrayAdapter<CharSequence> comunaAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.comunas, android.R.layout.simple_spinner_item);

        comunaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comunaSpinner.setAdapter(comunaAdapter);
        comunaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, android.view.View v, int position, long id) {
                Bundle b = new Bundle();
                if (parent.getSelectedItemId() != 0) {
                    //Object com = parent.getItemAtPosition(position);
                    userComuna = "Comuna " + (position+1);
                    b.putString("comuna", "comuna " + position);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button btnSave = (Button) getView().findViewById(R.id.BtnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
//                SharedPreferences prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = prefs.edit();
//                editor.putInt("objective", objectiveRadio.getCheckedRadioButtonId());
//                editor.putBoolean("otherPreference", otherPreference.isChecked());
//                editor.commit();
                try {
                    if (userJson != null) {
                        updateUser();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateUser() throws JSONException {
        final FragmentActivity activity = this.getActivity();

        ApiSrv.updateUser((String) userJson.get("_id"), wantToBeVolunteer.isChecked(), userComuna, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                Toast.makeText(getContext(), R.string.save, Toast.LENGTH_LONG).show();

                SharedPreferences prefs = activity.getSharedPreferences("prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user", responseBody.toString());
                editor.commit();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(getString(R.string.updateUser), "failure: " + responseString);
                Log.e(getString(R.string.updateUser), "failurecode: " + statusCode);
            }
        });
    }
}
