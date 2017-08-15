package com.driveembetter.proevolutionsoftware.driveembetter;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

import static java.security.AccessController.getContext;

/**
 * Created by matti on 13/08/2017.
 */

public class LocationUpdater {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double latitude, longitude;
    Activity activity;
    private String android_id;

    LocationUpdater(Activity activity) {
        this.activity = activity;
    }

    public LocationListener getLocationListener() {
        return this.locationListener;
    }

    public LocationManager getLocationManager() {
        return this.locationManager;
    }


    public void updateLocation() {

        if (ContextCompat.checkSelfPermission(activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Log.e("DB", "PERMISSION GRANTED");
        }

        android_id = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users/"+android_id);
        // Get LocationManager object from System Service LOCATION_SERVICE
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                Map<String, Object> coordinates = new ArrayMap<>();
                coordinates.put("lat", latitude);
                coordinates.put("lon", longitude);
                myRef.updateChildren(coordinates);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }
}
