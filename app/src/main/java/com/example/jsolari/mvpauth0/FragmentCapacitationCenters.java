package com.example.jsolari.mvpauth0;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class FragmentCapacitationCenters extends Fragment {

    public ListView centersList;
    public static CapacitationCentersAdapter arrayAdapter;
    private ListView lstOpciones;

    private static final String BASE_URL = "https://sos-api-qa.herokuapp.com";
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    private ArrayList<CapacitationCenterItem> datos = new ArrayList<CapacitationCenterItem>();

    public FragmentCapacitationCenters() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_capacitation_centers, container, false);
    }


    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
//        lstListado = (ListView)getView().findViewById(R.id.LstListado);
//
//        lstListado.setAdapter(new AdaptadorCorreos(this));
//
//        lstListado.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> list, View view, int pos, long id) {
//                if (listener!=null) {
//                    listener.onCorreoSeleccionado(
//                            (Correo)lstListado.getAdapter().getItem(pos));
//                }
//            }
//        });
        arrayAdapter = new CapacitationCentersAdapter(this, datos);
        centersList = (ListView)getView().findViewById(R.id.centersList);
        centersList.setAdapter(arrayAdapter);

        RequestParams params = new RequestParams();
        client.get(getAbsoluteUrl("/capacitation-centers"), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                Log.d("Centers", responseBody.toString());

                for (int i = 0; i < responseBody.length(); i++ ) {
                    String name = null;
                    String description = null;
                    try {
                        JSONObject item = responseBody.getJSONObject(i);
                        name = item.getString("name");
                        description = item.getString("description");
                        arrayAdapter.add(new CapacitationCenterItem(name, description));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

    class CapacitationCentersAdapter extends ArrayAdapter<CapacitationCenterItem> {

        Activity context;

        public CapacitationCentersAdapter(Fragment context, ArrayList<CapacitationCenterItem> datos) {
            super(context.getActivity(), R.layout.item_capacitation_center, datos);
            this.context = context.getActivity();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View item = inflater.inflate(R.layout.item_capacitation_center, null);

            TextView lblTitulo = (TextView)item.findViewById(R.id.title);
            lblTitulo.setText(datos.get(position).getTitle());

            TextView lblSubtitulo = (TextView)item.findViewById(R.id.body);
            lblSubtitulo.setText(datos.get(position).getBody());

            return(item);
        }
    }

    public static void UpdateCenters(String title, String body){
        arrayAdapter.add(new CapacitationCenterItem(title, body));
    }
}
