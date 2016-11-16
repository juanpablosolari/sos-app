package com.example.jsolari.mvpauth0;

<<<<<<< HEAD
import android.graphics.Color;
=======
>>>>>>> e96e38281d68be7b96e22163f37c965f4c864087
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
<<<<<<< HEAD
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
=======
>>>>>>> e96e38281d68be7b96e22163f37c965f4c864087

public class FragmentMap extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onActivityCreated(state);
        // setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
<<<<<<< HEAD


=======
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true); //Botonera de Zoom
        mMap.setTrafficEnabled(false); //Mostar trafico
        mMap.getUiSettings().setMapToolbarEnabled(true); //Botonera del Toolbar
        mMap.getUiSettings().setMyLocationButtonEnabled(true); //Boton de Location


        LatLng davinci = new LatLng(-34.604346, -58.395783);
        mMap.addMarker(new MarkerOptions().position(davinci).title("Escuela Da Vinci"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(davinci, 15.0f)); //Esto deberia apuntar a la Latitud y Longitud del Voluntario


        LatLng avaya = new LatLng(-34.603114, -58.393598);
        mMap.addMarker(new MarkerOptions().position(avaya).title("Avaya"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(avaya));

>>>>>>> e96e38281d68be7b96e22163f37c965f4c864087
    }
}
