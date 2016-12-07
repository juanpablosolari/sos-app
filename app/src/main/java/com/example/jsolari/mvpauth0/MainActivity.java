package com.example.jsolari.mvpauth0;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.example.jsolari.mvpauth0.utils.CredentialsManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private Toolbar appbar;
    private DrawerLayout drawerLayout;
    private NavigationView navView;

    private static final String TAG = "MainActivity";
    private static final String LOGTAG = "android-localizacion";

    private Button btnEmergency;
    private Button btnCancelEmergency;
    public static FrameLayout content_frame;
    public static FrameLayout map;
    private static GoogleMap mMap;

    //Localizacion
    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private static GoogleApiClient apiClient;
    //--Localizacion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appbar = (Toolbar) findViewById(R.id.appbar);
            setSupportActionBar(appbar);

        new AsyncTaskEmergencies().execute(this);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_nav_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

//        MenuItem item = (MenuItem) findViewById(R.id.profile);
//        if (user == null) {
//            item.setVisible(false);
//        } else {
//            item.setVisible(true);
//        }

        content_frame = (FrameLayout) findViewById(R.id.content_frame);
        map = (FrameLayout) findViewById(R.id.map);

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
                                showMap();
                                content_frame.setVisibility(View.GONE);
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
                            case R.id.profile:
                                fragment = new FragmentProfile();
                                fragmentTransaction = true;
                                break;
                            case R.id.logout:
                                CredentialsManager.deleteCredentials(MainActivity.this);
                                intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                Toast.makeText(MainActivity.this, "Sesion finalizada", Toast.LENGTH_SHORT).show();
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


        btnEmergency = (Button) findViewById(R.id.btnEmergency);
        btnCancelEmergency = (Button) findViewById(R.id.btnCancelEmergency);
        btnCancelEmergency.setVisibility(View.GONE);
        btnEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEmergency.setVisibility(View.GONE);
                btnCancelEmergency.setVisibility(View.VISIBLE);
                startTimer();
            }
        });
        btnCancelEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEmergency.setVisibility(View.VISIBLE);
                stopTimer();
            }
        });
    }

    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();
    private int count = 5;

    private void stopTimer() {
        if (mTimer1 != null) {
            btnEmergency.setVisibility(View.VISIBLE);
            btnCancelEmergency.setVisibility(View.GONE);
            count = 6;
            mTimer1.cancel();
            mTimer1.purge();
        }
    }

    private void startTimer() {
        mTimer1 = new Timer();
        mTt1 = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run() {
                        btnCancelEmergency.setText(getString(R.string.callCancel) + count);
                        count--;
                        if (count == 0) {
                            stopTimer();
                            sendNotification();
                        }
                    }
                });
            }
        };

        mTimer1.schedule(mTt1, 1, 1000);
    }

    public void sendNotification() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location loc = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        if (loc != null) {
            FragmentEmergencies.sendEmergency(loc);
            Toast.makeText(MainActivity.this, R.string.callSame, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, R.string.GPSDisableCallSame, Toast.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String phone = "107";
                String temp = "tel:" + phone;
                intent.setData(Uri.parse(temp));
                startActivity(intent);
                btnEmergency.setVisibility(View.VISIBLE);
            }
        }, 1000);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true); //Botonera de Zoom
        mMap.setTrafficEnabled(false); //Mostar trafico
        mMap.getUiSettings().setMapToolbarEnabled(true); //Botonera del Toolbar
        mMap.setMyLocationEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        /*LatLng obelisco = new LatLng(-34.604346, -58.395783);
        mMap.addMarker(new MarkerOptions().position(obelisco).title("Escuela Da Vinci"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(obelisco, 14.0f));

        LatLng avaya = new LatLng(-34.602629, -58.393655);
        mMap.addMarker(new MarkerOptions().position(avaya).title("Avaya"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(avaya));
        mMap.setTrafficEnabled(false);*/

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
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location loc = LocationServices.FusedLocationApi.getLastLocation(apiClient);

        if (loc != null) {
            LatLng avaya = new LatLng(loc.getLatitude(), loc.getLongitude());
            //mMap.addMarker(new MarkerOptions().position(avaya).title("Tu posiciòn"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(avaya, 16.0f));
            mMap.setTrafficEnabled(false);
        } else {
            Toast.makeText(MainActivity.this, R.string.GPSDisable, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @SuppressWarnings("ResourceType")
    public static void showMapMarker(final EmergencyItem item) throws JSONException {
        Bundle bundle = new Bundle();
        bundle.putString("EmergencyItem", item.toString());
        JSONObject location = new JSONObject(item.getLocation());
        showMap();
        JSONObject geometry = location.getJSONObject("geometry");
        JSONObject loc = geometry.getJSONObject("location");
        double lng = loc.getDouble("lng");
        double lat = loc.getDouble("lat");

        Log.e("item", location.toString());

        final LatLng destination = new LatLng(lat, lng);

        Location myLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

        //double distance = SphericalUtil.computeDistanceBetween(myLatLng, destination);

        ApiSrv.getDistanceBetween(myLatLng, destination, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                Marker marker = null;

                if (responseBody != null) {
                    Log.d("getDistanceBetween", responseBody.toString());
                    //D/getDistanceBetween: {"destination_addresses":["Gascón 36, Cdad. Autónoma de Buenos Aires, Argentina"],"origin_addresses":["Gascón 34, C1181ABB CABA, Argentina"],
                    // "rows":[{"elements":[{"distance":{"text":"8 m","value":8},"duration":{"text":"1 min","value":5},"status":"OK"}]}],"status":"OK"}
                    String distance = "no se pudo calcular";
                    String duration = "no se pudo calcular";

                    try {
                        JSONObject element = responseBody.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);
                        distance = element.getJSONObject("distance").getString("text");
                        duration = element.getJSONObject("duration").getString("text");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String address = item.getBody().split(",")[0];
                    marker = mMap.addMarker(new MarkerOptions().position(destination).title(address + ", Distancia " + distance + " en " + duration));
                } else {
                    marker = mMap.addMarker(new MarkerOptions().position(destination).title(item.getBody()));
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 16.0f));
                mMap.setTrafficEnabled(false);
                marker.showInfoWindow();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("getDistanceBetween",  "failure: " + responseString);
                Log.e("getDistanceBetween",  "failurecode: " + statusCode);
            }
        });
    }

    public static void showMap() {
        content_frame.setVisibility(View.GONE);
        map.setVisibility(View.VISIBLE);
    }
}