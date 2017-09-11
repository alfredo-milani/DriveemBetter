package com.driveembetter.proevolutionsoftware.driveembetter.utils;


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
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by matti on 28/08/2017.
 */

public class PositionManager
        extends Application
        implements Constants {

    private final static String TAG = PositionManager.class.getSimpleName();

    private static PositionManager positionManager;
    private SingletonUser user;
    private Geocoder geocoder;
    private Context context;

    // Singleton
    private PositionManager(Context context) {
        this.context = context;
        this.user = SingletonUser.getInstance();
        this.geocoder = new Geocoder(context, Locale.ENGLISH);
        this.updatePosition();
    }

    public static PositionManager getInstance(Context context) {
        if (positionManager == null) {
            positionManager = new PositionManager(context);
        }
        return positionManager;
    }



    public boolean isGPSEnabled() {
        final LocationManager manager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void updatePosition() {
        LocationManager locationManager = (LocationManager) this.context.getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this.context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.context,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this.context,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Log.d("DB", "PERMISSION GRANTED");
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (user == null || user.getUid().isEmpty()) {
                    return;
                }

                String oldCountry = user.getCountry();
                String oldRegion = user.getRegion();
                String oldSubRegion = user.getSubRegion();

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                final String[] strings = getLocationFromCoordinates(latitude, longitude, 1);

                // Update SingletonUser state
                user.setLatitude(latitude);
                user.setLongitude(longitude);
                user.setCountry(strings[0]);
                user.setRegion(strings[1]);
                user.setSubRegion(strings[2]);
                Log.d(TAG, "Position: " + user.getCountry() + " / " + user.getRegion() + " / " + user.getSubRegion());

                if (!oldSubRegion.equals(user.getSubRegion()) || !oldRegion.equals(user.getRegion()) ||
                        !oldCountry.equals(user.getCountry())) {
                    // Remove user from position node only if his position has changed
                    FirebaseDatabaseManager.removeOldPosition(new String[] {oldCountry, oldRegion, oldSubRegion});
                    // Create user in new position
                    FirebaseDatabaseManager.createNewUserPosition();
                }

                if (user.getSubRegion().equals(SUB_REGION) || user.getRegion().equals(REGION) ||
                        user.getCountry().equals(COUNTRY)) {
                    user.setAvailability(UNAVAILABLE);
                } else {
                    user.setAvailability(AVAILABLE);
                }

                // Update position node
                FirebaseDatabaseManager.upPositionCoordAndAvail();
                // Update users node
                FirebaseDatabaseManager.updateUserCoordAndAvail();
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

    /**
     * Get Location from coordinates
     * @param latitude latitude
     * @param longitude longitude
     * @param maxResult result's number
     * @return string[0] --> Nation; string[1] --> Region; string[2] --> District
     */
    public String[] getLocationFromCoordinates(double latitude, double longitude, int maxResult) {
        String[] strings = new String[3];
        try {
            List<Address> addresses = this.geocoder.getFromLocation(latitude, longitude, maxResult);

            strings[0] = addresses.get(0).getCountryName();
            strings[1] = addresses.get(0).getAdminArea();
            strings[2] = addresses.get(0).getSubAdminArea();
        } catch (IOException e) {
            e.printStackTrace();

            strings[0] = COUNTRY;
            strings[1] = REGION;
            strings[2] = SUB_REGION;
        }
        return strings;
    }
}