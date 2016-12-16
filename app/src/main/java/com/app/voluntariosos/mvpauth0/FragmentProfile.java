package com.app.voluntariosos.mvpauth0;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class FragmentProfile extends Fragment {

    private static ApiSrv ApiSrv = new ApiSrv();
    private SharedPreferences prefs;
    public JSONObject userJson = null;
    public EditText nameField = null;
    public EditText lastnameField = null;
    public EditText phoneField = null;
    public EditText dniField = null;
    public RadioGroup genderField = null;
    public String gender = null;

    public FragmentProfile() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        prefs = this.getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String user = prefs.getString("user", "{}");

        nameField = (EditText) getView().findViewById(R.id.nameField);
        lastnameField = (EditText) getView().findViewById(R.id.lastnameField);
        phoneField = (EditText) getView().findViewById(R.id.phoneField);
        dniField = (EditText) getView().findViewById(R.id.dniField);
        genderField = (RadioGroup) getView().findViewById(R.id.gender);

        try {
            userJson = new JSONObject(user);
            if (userJson != null) {
                nameField.setText(userJson.getString("firstName"));
                lastnameField.setText(userJson.getString("lastName"));
                phoneField.setText(userJson.getString("phone"));
                dniField.setText(userJson.getString("dni"));
                gender = userJson.getString("gender");

                RadioButton genderMale = (RadioButton) getView().findViewById(R.id.genderMale);
                RadioButton genderFemale = (RadioButton) getView().findViewById(R.id.genderFemale);

                if (gender.equals("male")) {
                    genderMale.setChecked(true);
                } else if (gender.equals("female")) {
                    genderFemale.setChecked(true);
                }

                 if (!userJson.has("acceptedTerms") || userJson.getBoolean("acceptedTerms") == false) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle(R.string.terms)
                        .setMessage("VoluntarioSOS (VSOS) no se hace responsable de los hechos que haya o pueda haber ocurrido en el Incidente.\n" +
                                    "VSOS no se hace responsable de lo que pueda sucederle a la persona que se encuentra en emergencia.\n" +
                                    "VSOS recomienda no tocar al accidentado, solo esperar que la ambulancia llegue.")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            try {
                                userJson.put("acceptedTerms", "true");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            dialog.cancel();
                            }
                        });

                    builder.create().show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Button btnSave = (Button) getView().findViewById(R.id.BtnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
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

    public Dialog onConfirmDialog(DialogInterface.OnClickListener cb) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.terms)
                .setPositiveButton(R.string.si, cb)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(getString(R.string.dialogos), getString(R.string.confirmCanceled));
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    public void updateUser() throws JSONException {
        final FragmentActivity activity = this.getActivity();
        userJson.put("firstName", nameField.getText());
        userJson.put("lastName", lastnameField.getText());
        userJson.put("phone", phoneField.getText());
        userJson.put("dni", dniField.getText());

        String opcion = "";
        switch(genderField.getCheckedRadioButtonId()) {
            case R.id.genderMale:
                opcion = "male";
                break;
            case R.id.genderFemale:
                opcion = "female";
                break;
        }

        userJson.put("gender", opcion);

        ApiSrv.updateUserProfile((String) userJson.get("_id"), userJson, new JsonHttpResponseHandler() {

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
