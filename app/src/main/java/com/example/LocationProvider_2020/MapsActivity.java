package com.example.LocationProvider_2020;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    List<Location> savedLocations;
    ////**Attribute for coordinate**////
    private double latitude_map;
    private double longitude_map;

    /**Method onCreate Fragment Activity**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Catch the coordinate by primary Activity
        Intent i= getIntent();
        latitude_map = i.getDoubleExtra("latitude", 0);
        longitude_map= i.getDoubleExtra("longitude", 0);

        //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //Myapplication myapplication = (Myapplication)getApplicationContext();
        //savedLocations=myapplication.getMyLocations();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Object of coordinate
        UiSettings mUiSetting;
        mUiSetting =googleMap.getUiSettings();
        mUiSetting .setZoomControlsEnabled(true);
        mUiSetting.setCompassEnabled(true);
        mUiSetting.setMyLocationButtonEnabled(true);

       //googleMap.setMapType (/*Style/);
       //googleMap.setMapStyle(/*Style/);
       //googleMap.setTrafficEnabled(/*Style/);

        LatLng position_marker = new LatLng(latitude_map, longitude_map);
        //Object marker [to set te marker option (position, title etc...)
        //googleMap.addMarker(new MarkerOptions().position(latitude_map, longitude_map));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position_marker);
        markerOptions.title("My Position"); //A method that add the marker on the map



        //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastlocationPlaced, 10.0f));


    }
}


