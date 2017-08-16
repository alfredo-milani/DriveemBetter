package com.driveembetter.proevolutionsoftware.driveembetter;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
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
    private double latitude, longitude, oldLatitude, oldLongitude;
    private String oldCountry, oldRegion;
    Location oldLocation;
    Activity activity;
    private String android_id;
    private Geocoder geocoder;

    //TODO
    //---- I have to get these information from an other db------
    /**/    private List veichlesArray;                      /**/
    /**/    private String currentVeichle;                   /**/
    /**/    private String veichleType;                      /**/
    /**/    private String userImage;                        /**/
    //-----------------------------------------------------------

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

        final Geocoder geocoder;
        geocoder = new Geocoder(activity.getApplicationContext(), Locale.ENGLISH);


        android_id = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        database = FirebaseDatabase.getInstance();
        // Get LocationManager object from System Service LOCATION_SERVICE
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        oldLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        oldLatitude = oldLocation.getLatitude();
        oldLongitude = oldLocation.getLongitude();
        List<Address> oldAddresses;
        try {
            oldAddresses = geocoder.getFromLocation(oldLatitude, oldLongitude, 1);
            oldCountry = oldAddresses.get(0).getCountryName();
            oldRegion = oldAddresses.get(0).getAdminArea();
        } catch (IOException e) {
            e.printStackTrace();
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                List<Address> addresses;
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Map<String, Object> coordinates = new ArrayMap<>();
                coordinates.put("currentUserPosition", latitude+";"+longitude);
                String country = "";
                String region = "";
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    country = addresses.get(0).getCountryName();
                    region = addresses.get(0).getAdminArea();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //coordinates.put("lat", latitude);
                //coordinates.put("lon", longitude);
                Boolean radicalChange = false;
                if (!oldCountry.equals(country) || !oldRegion.equals(region)) {
                    //delete the current user instance and create a newer
                    myRef = database.getReference(oldCountry + "/" + oldRegion + "/" + "ID_USER(from firebase)");
                    myRef.removeValue();
                    radicalChange = true;
                    //TODO
                    //If I delete, I have to regenerate lost information
                }
                oldCountry = country;
                oldRegion = region;
                myRef = database.getReference(country + "/" + region + "/" + "ID_USER(from firebase)");
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
