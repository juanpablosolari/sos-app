package com.example.jsolari.mvpauth0;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import static com.example.jsolari.mvpauth0.R.id.lblLatitud;
import static com.example.jsolari.mvpauth0.R.id.lblLongitud;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private Toolbar appbar;
    private DrawerLayout drawerLayout;
    private NavigationView navView;

    private static final String TAG = "MainActivity";
    private static final String LOGTAG = "android-localizacion";

    //Localizacion
    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private GoogleApiClient apiClient;
    //--Localizacion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(appbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_nav_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();


        navView = (NavigationView) findViewById(R.id.navview);
        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        boolean fragmentTransaction = false;
                        Fragment fragment = null;
                        Intent intent;

                        switch (menuItem.getItemId()) {
                            case R.id.login:
                                intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.map:
                                getSupportActionBar().setTitle("Mapa");
                                menuItem.setChecked(true);
                                FrameLayout content_frame = (FrameLayout) findViewById(R.id.content_frame);
                                content_frame.setVisibility(View.GONE);
                                FrameLayout map = (FrameLayout) findViewById(R.id.map);
                                map.setVisibility(View.VISIBLE);
                                break;
                            case R.id.capacitationCenters:
                                fragment = new FragmentCapacitationCenters();
                                fragmentTransaction = true;
                                break;
                            case R.id.emergencies:
                                fragment = new FragmentEmergencies();
                                fragmentTransaction = true;
                                break;
                        }

                        if (fragmentTransaction) {
                            FrameLayout layout = (FrameLayout) findViewById(R.id.map);
                            layout.setVisibility(View.GONE);
                            FrameLayout content_frame = (FrameLayout) findViewById(R.id.content_frame);
                            content_frame.setVisibility(View.VISIBLE);

                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, fragment)
                                    .commit();

                            menuItem.setChecked(true);
                            getSupportActionBar().setTitle(menuItem.getTitle());
                        }

                        drawerLayout.closeDrawers();

                        return true;
                    }
                });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Button btnEmergency = (Button) findViewById(R.id.btnEmergency);
        btnEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location loc = LocationServices.FusedLocationApi.getLastLocation(apiClient);
                FragmentEmergencies.sendEmergency(String.valueOf(loc.getLatitude()), String.valueOf(loc.getLongitude()));
            }

        });
    }

    //    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private GoogleMap mMap;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true); //Botonera de Zoom
        mMap.setTrafficEnabled(false); //Mostar trafico
        mMap.getUiSettings().setMapToolbarEnabled(true); //Botonera del Toolbar


        LatLng obelisco = new LatLng(-34.604346, -58.395783);
        mMap.addMarker(new MarkerOptions().position(obelisco).title("Escuela Da Vinci"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(obelisco, 14.0f));

        LatLng avaya = new LatLng(-34.602629, -58.393655);
        mMap.addMarker(new MarkerOptions().position(avaya).title("Avaya"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(avaya));
        mMap.setTrafficEnabled(false);

        /*
        //Trazar linea de un punto a otro
        PolylineOptions rectOptions = new PolylineOptions()
                .add(obelisco)
                .add(davinci)
                .color(Color.BLUE)
                .geodesic(true)
                .width(10f);

        Polyline polyline = mMap.addPolyline(rectOptions);

        //Radio de un punto
        CircleOptions circleOptions = new CircleOptions()
                .center(obelisco)
                .radius(500);

        //Radio de un punto
        Circle circle = mMap.addCircle(circleOptions);
        */


    }



    public static void showEmergencyToast(String text) {
        //Toast.makeText(new MainActivity(), text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}