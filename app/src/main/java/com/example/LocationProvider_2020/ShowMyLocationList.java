package com.example.LocationProvider_2020;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Calendar;
import java.util.List;

public class ShowMyLocationList extends AppCompatActivity {
    /**GUI**/
    ListView lv_savedLocations;
    /**List Location of WayPoint**/
    List<Location> locationList_waypoint;
    List<String> waypoint_readable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_location_list);
        /**Setting Gui**/
        lv_savedLocations = findViewById(R.id.location_list);

        /**Recover the List Location of WayPoint**/
        Myapplication myapplication = (Myapplication) getApplication();
        locationList_waypoint = myapplication.getMyLocations();
        waypoint_readable = myapplication.getMyLocations_string();

        /**Put the Location on the list**/
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_savedlocation_list, R.id.textview_forlist,waypoint_readable);
        lv_savedLocations.setAdapter(arrayAdapter);
    }

}
