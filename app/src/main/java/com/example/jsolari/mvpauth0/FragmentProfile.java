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

    private static ApiSrv ApiSrv = new ApiSrv();
    private SharedPreferences prefs;
    public JSONObject userJson = null;

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
        String user = prefs.getString("user", "");
    }

    public void updateUser() throws JSONException {
        final FragmentActivity activity = this.getActivity();
//
//        ApiSrv.updateUser((String) userJson.get("_id"), wantToBeVolunteer.isChecked(), userComuna, new JsonHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
//                super.onSuccess(statusCode, headers, responseBody);
//                Toast.makeText(getContext(), R.string.save, Toast.LENGTH_LONG).show();
//
//                SharedPreferences prefs = activity.getSharedPreferences("prefs", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = prefs.edit();
//                editor.putString("user", responseBody.toString());
//                editor.commit();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                super.onFailure(statusCode, headers, responseString, throwable);
//                Log.e(getString(R.string.updateUser), "failure: " + responseString);
//                Log.e(getString(R.string.updateUser), "failurecode: " + statusCode);
//            }
//        });
    }
}
