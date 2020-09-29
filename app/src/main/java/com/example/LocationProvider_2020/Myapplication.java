package com.example.LocationProvider_2020;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Myapplication extends Application {

    private static Myapplication singleton;

    /**Attribute**/
    private List<Location> myLocations;
    private List<String> myLocations_string;

    /**Method**/
    public List<Location> getMyLocations() {
        return myLocations;
    }
    public List<String> getMyLocations_string() {
        return myLocations_string;
    }

    public void setMyLocations(List<Location> myLocations) {
        this.myLocations = myLocations;
    }
    public void setMyLocations_string(List<String> myLocations_string) {this.myLocations_string = myLocations_string; }

    public static Myapplication getSingleton() {
        return singleton;
    }

    public void onCreate(){
        super.onCreate();
        singleton= this;   // this class is properties of singleton
        myLocations = new ArrayList<>();
        myLocations_string = new ArrayList<>();
    }
}
