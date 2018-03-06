package com.drwitteck.mymappappdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    LocationManager lm;
    LocationListener ll;
    Location currentLocation;
    TextView lat;
    TextView longitude;
    MapView mapView;
    GoogleMap googleMap;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        /**
         * in each lifecycle call back must have mapView.CallBack(); that corresponds
         */
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        findViewById(R.id.dropPinButton).setOnClickListener(v -> {
            MarkerOptions marker = new MarkerOptions().position(latLng).title("My Location");
            googleMap.addMarker(marker);
        });

        lat = findViewById(R.id.latText);
        longitude = findViewById(R.id.longitudeText);

        ll = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latLng = new LatLng(location.getLatitude(),location.getLongitude());
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng,16);
                googleMap.animateCamera(cu);

                currentLocation = location;
                lat.setText(location.getLatitude()+"");
                longitude.setText(location.getLongitude()+"");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onProviderDisabled(String provider) {}
        };
    }

    @SuppressWarnings("MissingPermission")
    private void requestUpdates(){
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,ll);
        lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,0,0,ll);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            requestUpdates();
        }else{
            Toast.makeText(this, "Not granted.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mapView.onResume();
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION ) ==
                PackageManager.PERMISSION_GRANTED){
            requestUpdates();
        }else{
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }

    }

    @Override
    protected void onPause(){
        super.onPause();
        mapView.onPause();
        lm.removeUpdates(ll);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
}
