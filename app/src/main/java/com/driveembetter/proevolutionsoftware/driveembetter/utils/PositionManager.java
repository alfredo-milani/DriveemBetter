package com.driveembetter.proevolutionsoftware.driveembetter.utils;


import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonTwitterProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.MainFragmentActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.twitter.sdk.android.core.models.TwitterCollection;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by matti on 28/08/2017.
 */

public class PositionManager extends Application {

    private double latitude = 0;
    private double longitude = 0;
    private double oldLatitude, oldLongitude;
    private String oldCountry, oldRegion;
    private static PositionManager positionManager;
    private Activity activity;
    private String userId, email;
    private Geocoder geocoder;
    private FirebaseDatabase database;
    private Map<String, Object> emailMap;
    Location oldLocation;
    private DatabaseReference myRef, checkRef;


    //Singleton
    private PositionManager(Activity activity) {
        this.activity = activity;
        updatePosition();
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }


    public static PositionManager getInstance(Activity activity) {
        if (positionManager == null) {
            positionManager = new PositionManager(activity);
        }
        return positionManager;
    }

    private void updatePosition() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
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

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        geocoder = new Geocoder(activity.getApplicationContext(), Locale.ENGLISH);
        database = FirebaseDatabase.getInstance();
        oldLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (oldLocation != null) {
            oldLatitude = oldLocation.getLatitude();
            oldLongitude = oldLocation.getLongitude();

            List<Address> oldAddresses;
            try {
                oldAddresses = geocoder.getFromLocation(oldLatitude, oldLongitude, 1);
                if (oldAddresses.size()!=0) {
                    oldCountry = oldAddresses.get(0).getCountryName();
                    oldRegion = oldAddresses.get(0).getAdminArea();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            oldCountry = "oldCountry";
            oldRegion = "oldRegion";
        }


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                List<Address> addresses;
                Map<String, Object> coordinates = new ArrayMap<>();
                emailMap = new ArrayMap<>();
                coordinates.put("currentUserPosition", latitude+";"+longitude);
                emailMap.put("email", email);
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
                    myRef = database.getReference(oldCountry + "/" + oldRegion + "/" + userId);
                    myRef.removeValue();
                    radicalChange = true;
                }
                if (radicalChange) {
                    //I have to add old user information
                    radicalChange = false;

                }
                oldCountry = country;
                oldRegion = region;
                myRef = database.getReference(country + "/" + region + "/" + userId);
                checkRef = database.getReference(country + "/" + region + "/" + userId);

                myRef.updateChildren(coordinates);
                checkRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild("email")) {
                            myRef.updateChildren(emailMap);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
        });
    }




}
