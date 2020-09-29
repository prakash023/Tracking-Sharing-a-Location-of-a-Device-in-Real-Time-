package com.example.LocationProvider_2020;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient; // since the import is not automatically provided we need to import manually
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnSuccessListener;



import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/////*******************************************************MainActivity*******************************************************//////
public class MainActivity extends AppCompatActivity {

    ////**Attribute**////
    public static final int UPDATE_INTERVAL = 1000;
    public static final int FAST_UPDATE_INTERVAL = 500;
    private static final int PERMISSION_FINE_LOCATION = 99;
    public static final int REQUEST_CHECK_SETTINGS = 0;
    private static GoogleApiClient mGoogleApiClient;

    ////****GUI Object****////
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address, tv_countwaypoints; //ignoring all the static labels
    Button btn_newWayPoint, btn_showWayPointList, btn_showMap, btn_sharePos;
    Switch sw_locationupdates, sw_gps;

    ////****Location Object****////
    Location currentLocation; // from here we get the current location longitude and latitude which we have created through callback function--> 339 updateGPS
    List<Location> savedLocations;  //list of saved location
    List<String> savedLocations_string; //list of saved location (readable)
    LocationRequest locationRequest; // related to fused location provider client
    LocationCallback locationCallback;

    FusedLocationProviderClient fusedLocationProviderClient; //main source for providing the location using this class

    ////**Coordinate for Map**////
    public double latitude_for_map;
    public double longitude_for_map;

    ////**Address for sharing*////
    public String address_share;

    ////**Flag**////
    public boolean GPS_sensor_status;

    /************************Method OnResume*********************/
    @Override
    protected void onResume(){
        super.onResume();

    }
    /************************Method OnCreate*********************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        savedLocations = new ArrayList<>();
        savedLocations_string = new ArrayList<>();

        // defining the UI variables with a value
        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_speed = findViewById(R.id.tv_speed);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        sw_gps = findViewById(R.id.sw_gps);
        sw_locationupdates = findViewById(R.id.sw_locationsupdates);
        btn_newWayPoint = findViewById(R.id.button_newWayPoint);
        btn_showWayPointList = findViewById(R.id.button2);
        tv_countwaypoints = findViewById(R.id.tv_waypointcounts);
        btn_showMap = findViewById(R.id.btn_showMap);
        btn_sharePos = findViewById(R.id.btn_sharePosition);

        //Set the settings to get the location with locationRequest
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL); //default location update time
        locationRequest.setFastestInterval(FAST_UPDATE_INTERVAL); // for updating the request_time
        locationRequest.setPriority(locationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        //Set the callback (Action after the location is ready) with Override method of locationCallback's object
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                updateUIValues(location);
            }
        };


        /////****Click button event setting****////
        btn_newWayPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting waypoints
                savedLocations.add(currentLocation);
                //getting waypoint (String form)
                String currentLocation_string = "Lat: " + convertLatLon(currentLocation.getLatitude()) + " Lon" + convertLatLon(currentLocation.getLongitude()) + " Adress" + tv_address.getText();
                savedLocations_string.add(currentLocation_string);

                tv_countwaypoints.setText(Integer.toString(savedLocations.size())); //Update Location Counter
            }
        });
        btn_showWayPointList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ShowMyLocationList.class);
                //Use Object Application to pass complex object (Location List)between Activity
                Myapplication myapplication = (Myapplication) getApplication();
                myapplication.setMyLocations(savedLocations);   //Savelocation during the program are updated if you can touch the AddNewWayPoint Button (line 107)
                myapplication.setMyLocations_string(savedLocations_string); //SaveLocation_string are updated along with saveLocations
                startActivity(i);
            }
        });
        btn_showMap.setOnClickListener(new View.OnClickListener() {        //This button show the map and the position on map
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                i.putExtra("latitude", latitude_for_map);   //send data to another Activity
                i.putExtra("longitude", longitude_for_map);
                startActivity(i);
            }
        });

        /////****Click switch event setting****////
        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_gps.isChecked()) {

                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //for accurate GPS
                    tv_sensor = (TextView) findViewById(R.id.tv_sensor); // error on switches through null pointer exception which are disabled here else the apps crashes
                    tv_sensor.setText("GPS");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    //tv_sensor = (TextView) findViewById(R.id.tv_sensor);
                    tv_sensor.setText("Towers & WIFI");
                }
            }
        });
        sw_locationupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_locationupdates.isChecked()) {
                    startLocationUpdates();
                } else {
                    stopLocationUpdates();
                }
            }
        });

        ////****Click Button to share position****/
        btn_sharePos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if there is a
                if(currentLocation!=null){
                    /**String creation that will be shared**/
                    String share_string = "This is my where i am now: " + address_share;

                    Intent intent = new Intent(Intent.ACTION_SEND); //I create a new Intent and pass ACTION_SEND flag to indicate the standard intent for share
                    intent.setType("text/plain"); //With this method i specify the app for share
                    intent.putExtra(Intent.EXTRA_TEXT, share_string); //I pass a text with putExtra and indicate the title with EXTRA_TEXT flag
                    startActivity(Intent.createChooser(intent, "Share my position")); //Start the Activity
                }else{
                    Toast.makeText(MainActivity.this, "Wait, the position isn't ready", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Request turn On GPS Service, inner this function will activated the GPS Update method only there if will be the GPS Service enabled
        initGoogleAPIClient();
        turnGPSon();

    }




    /************************Method STOP Location Updates (GUI)************************/
    private void stopLocationUpdates() {

        tv_updates.setText("Location tracking is halted");
        tv_lat.setText("Not tracking");
        tv_lon.setText("Not tracking");
        tv_sensor.setText("Not tracking");
        tv_address.setText("Not tracking");
        tv_speed.setText("Not tracking");
        tv_altitude.setText("Not tracking");
        tv_accuracy.setText("Not tracking");
        sw_gps.setClickable(false); //Stop the GPS Switch (if the position is "stop" there is no point in using GPS or WIFI)
        fusedLocationProviderClient.removeLocationUpdates(locationCallback); //Stop location Update

    }

    /************************Method START Location Updates************************/
    private void startLocationUpdates() {
        tv_updates.setText("Tracking the Location");
        //switch GPS restart like was before the disable of tracking
        sw_gps.setClickable(true);
        sw_gps.callOnClick();
        //restart service
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        updateGPS();
    }

    /************************Method REQUEST PERMISSION************************/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSION_FINE_LOCATION:
                //case PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    /******Update GPS Method******/
                    updateGPS();
                }
                else {
                    Toast.makeText(this, "Give permission for GPS", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    /************************Method REQUEST ENABLED GPS************************/
    public void turnGPSon() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient to show dialog always when GPS is off

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();

                switch (status.getStatusCode()) {
                    /**The GPS is already activated**/
                    case LocationSettingsStatusCodes.SUCCESS:
                        GPS_sensor_status = true;

                        /******Update GPS Method******/
                        updateGPS();
                    break;

                    /**The GPS isn't activate but it could be shown the dialog box to activate directly in the Activity**/
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {

                            e.printStackTrace();

                        }
                    break;

                    /**The GPS can't activated, we have no way to fix it (Broken or missing GPS module or the phone is in a airplane mode)**/
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        GPS_sensor_status = false;

                        Toast.makeText(MainActivity.this, "The GPS Service can't activated", Toast.LENGTH_SHORT).show();
                        finish();
                    break;
                }
            }
        });
        System.out.println(GPS_sensor_status);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**Create object for setting**/
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);  //LocationsettingStates is object that dialogue with the setting of enabled-disabled module

        /**Check the RequestCode and next the result Code to determinate the result of choice**/
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            //Result OK
            if (resultCode == Activity.RESULT_OK) {
                GPS_sensor_status = true;

                /******Update GPS Method******/
                updateGPS();
            }
            //Result refused
            if (resultCode == Activity.RESULT_CANCELED) {
                GPS_sensor_status = false;

                Toast.makeText(MainActivity.this, "You must enable the GPS service", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /*************************Method GETTER LOCATION ************************/
    private void updateGPS() {
        //Get location provider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        //Check Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this,  new OnSuccessListener<Location>() {   //Get Location and Update Gui
                @Override
                public void onSuccess(Location location) {
                    //updating here
                    updateUIValues(location); //--> this one creates news activity (138) below!!
                    currentLocation =location;
                    startLocationUpdates();
                }
            });
        }
        else {
            //There is not permission
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){   //Check the SDK correct version and required the permission

                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);  //required the permission
            }
        }
    }

    /************************Method UPDATE GUI VALUES************************/
    private void updateUIValues(Location location) {   // this one was called from the UpdateGPS values to update text view

        if(location!=null){         //Check if the object location is not null

            latitude_for_map= location.getLatitude();
            longitude_for_map= location.getLongitude();

            tv_lat.setText(String.valueOf(location.getLatitude()));
            tv_lon.setText(String.valueOf(location.getLongitude()));
            tv_accuracy.setText(String.valueOf(location.getAccuracy()));

            if (location.hasAltitude()) {
                tv_altitude.setText(String.valueOf(location.getAltitude())); // if the devices is integrated with altitude sensor
            }
            else {
                tv_altitude.setText("Not Available"); // if the devices doesn't have the function named Altitude
            }

            if (location.hasSpeed()){
                tv_speed.setText(convertLatLon(location.getSpeed()*3.6) + " km/h"); // if the devices is integrated with altitude sensor
            }
            else {
                tv_speed.setText("Not Available"); // if the devices doesn't have the function named Altitude
            }

            Geocoder geocoder = new Geocoder(MainActivity.this);

            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                tv_address.setText(addresses.get(0).getAddressLine(0));
                address_share = addresses.get(0).getAddressLine(0); //I put the first address on my variable (the method return a String object)
            }
            catch(Exception e) {
                tv_address.setText("Street Address Unavailable");
            }
        }
    }

    /************************Initiating Google API  ************************/
    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /************************Method CONVERTING THE LATITUDE AND LONGITUDE TO STRING WITH 2 DECIMAL NUMBER************************/
    private String convertLatLon(double x){
        String final_string;
        x = Math.round(x * 100);    //Round to 2 decimal
        x = x/100;
        final_string = String.valueOf(x);   //Convert to string
        return final_string;    //Return String
    }
} //Close the MainActivity class


