package com.app.voluntariosos.mvpauth0;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class FragmentVolunteer extends Fragment {
    private Spinner comunaSpinner;
    private ArrayAdapter<String> comunaAdapter;
    private Context applicationContext;
    private String comuna;
    private static ApiSrv ApiSrv = new ApiSrv();
    private SharedPreferences prefs;
    public JSONObject userJson = null;
    public Boolean isUserVolunteer = false;
    public String userComuna = "";
    public TextView MyComuna;
    public RadioGroup wantToBeVolunteer;
    public TextView isVolunteerTxt;
    public Boolean wantToBe = false;

    public FragmentVolunteer() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_volunteer, container, false);
    }

    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        prefs = this.getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String user = prefs.getString("user", "");
        wantToBeVolunteer = (RadioGroup) getView().findViewById(R.id.wantToBeVolunteer);
        isVolunteerTxt = (TextView) getView().findViewById(R.id.isVolunteerTxt);
        isVolunteerTxt.setVisibility(View.GONE);

        MyComuna = (TextView) getView().findViewById(R.id.MyComuna);
        //MyComuna.setText("Actualmente no pertenece a ninguna comuna.");

        try {
            userJson = new JSONObject(user);
            isUserVolunteer = userJson.getBoolean("isVolunteer");
            wantToBe = userJson.getBoolean("wantToBeVolunteer");
            userComuna = userJson.getString("comuna");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RadioButton affirmative = (RadioButton) getView().findViewById(R.id.affirmative);
        RadioButton negative = (RadioButton) getView().findViewById(R.id.negative);

        if (wantToBe.equals(true)) {
            affirmative.setChecked(true);
        } else {
            negative.setChecked(true);
        }

        if (isUserVolunteer.equals(true) && wantToBe.equals(true)) {
            isVolunteerTxt.setVisibility(View.VISIBLE);
            if (userComuna.equals("")) {
                MyComuna.setText("Actualmente usted NO pertence a ninguna comuna.");
            }else{
                MyComuna.setText("Actualmente usted pertenece a la " + userComuna);
            }
        }else{
            MyComuna.setText("");
        }

        comunaSpinner = (Spinner) getView().findViewById(R.id.comunaSpinner);
        ArrayAdapter<CharSequence> comunaAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.comunas, android.R.layout.simple_spinner_item);

        comunaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comunaSpinner.setAdapter(comunaAdapter);
        comunaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, android.view.View v, int position, long id) {
                if (parent.getSelectedItemId() != 0) {
                    userComuna = "Comuna " + (position+1);
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
        Boolean opcion = false;
        switch(wantToBeVolunteer.getCheckedRadioButtonId()) {
            case R.id.affirmative:
                opcion = true;
                break;
            case R.id.negative:
                opcion = false;
                break;
        }

        ApiSrv.updateUser((String) userJson.get("_id"), opcion, userComuna, new JsonHttpResponseHandler() {

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
