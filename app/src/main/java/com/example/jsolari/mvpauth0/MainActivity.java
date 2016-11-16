package com.example.jsolari.mvpauth0;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Toolbar appbar;
    private DrawerLayout drawerLayout;
    private NavigationView navView;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(appbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_nav_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        /*
        //Eventos del Drawer Layout
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        */

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
//                                fragment = new FragmentMap();
//                                fragmentTransaction = true;
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
                FragmentEmergencies.sendEmergency("Hola", "Chau");
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
<<<<<<< HEAD
=======
        mMap.getUiSettings().setZoomControlsEnabled(true); //Botonera de Zoom
        mMap.setTrafficEnabled(false); //Mostar trafico
        mMap.getUiSettings().setMapToolbarEnabled(true); //Botonera del Toolbar

>>>>>>> e96e38281d68be7b96e22163f37c965f4c864087

        LatLng davinci = new LatLng(-34.604346, -58.395783);
        mMap.addMarker(new MarkerOptions().position(davinci).title("Escuela Da Vinci"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(davinci, 14.0f));

        LatLng obelisco = new LatLng(-34.601646, -58.386752);
        mMap.addMarker(new MarkerOptions().position(obelisco).title("Avaya"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(obelisco));
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


}