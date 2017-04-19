package com.ristek.googlemaptutorial;

import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    // GoogleMap Variables
    private GoogleMap map;
    protected SupportMapFragment mapFragment;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeMap();
    }

    /**
     *
     * Google Maps methods group
     *
     */

    /**
     *
     * Method for initializing google maps fragment
     *
     */
    private void initializeMap() {
        if (map == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    /**
     *
     * Method to be called when google map is ready to be viewed
     *
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.map.setMyLocationEnabled(true);
        buildGoogleApiClient();
    }

    /**
     *
     * Method to build google API and connect it through google
     *
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     *
     * method to be called when google map fragment is connected
     *
     */
    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(100000000);
//        mLocationRequest.setFastestInterval(1000);
//        mLocationRequest.setMaxWaitTime(10000);
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     *
     * method to be called whenever location changed
     *
     */
    @Override
    public void onLocationChanged(Location location) {
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            String address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0).getThoroughfare();
            map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title(address));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12));
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "ERROR GETTING ADDRESS", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     * method to be called when marker was clicked
     *
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        double distance = SphericalUtil.computeDistanceBetween(new LatLng(marker.getPosition().latitude,marker.getPosition().longitude),new LatLng(-6.364601,106.828689));
        Toast.makeText(this, "a marker was clicked! distance is "+distance+" meters", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     *
     * method to be called whenever google maps connection was suspended
     *
     * onConnectionSuspended gets called when your app gets disconnected
     * from the Google Play services package (not necessarily the Internet).
     * The callback gets invoked for instance when you go to Settings > Apps > Google Play services > Force Stop.
     * Another example is when you would uninstall Google Play services.
     * You would get onConnectionSuspended followed by onConnectionFailed after a couple of seconds
     * (because a reconnection attempt would fail).
     */
    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     *
     * method to be called whenever google maps connection was failed
     * example : disconnected from the internet
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * End of Google Maps methods group
     */

}
