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
import com.driveembetter.proevolutionsoftware.driveembetter.entity.Mean;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.MeanDay;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.driveembetter.proevolutionsoftware.driveembetter.threads.RetrieveAndParseJSONPosition;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by matti on 28/08/2017.
 */

public class PositionManager extends Application
        implements Constants {

    private final static String TAG = PositionManager.class.getSimpleName();

    private static PositionManager positionManager;
    private static Geocoder geocoder;
    private static SingletonUser user;
    private LocationManager locationManager;
    private boolean listenerSetted = false;
    private Context context;
    private int speed;
    private double initialTime;
    private double time;
    private int initialSpeed = -1; //Symbolic value just to check when "onLocationChanged" is called for the first time
    private double deltaT;


    // Singleton
    private PositionManager(Context context) {
        this.context = context;
        this.user = SingletonUser.getInstance();
        PositionManager.geocoder = new Geocoder(context, Locale.ITALIAN);
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.updatePosition();
    }

    public static PositionManager getInstance(Context context) {
        if (positionManager == null) {
            positionManager = new PositionManager(context);
        }
        return positionManager;
    }

    private void updateStatistics(double speed) {

        //TRY
        MeanDay mean2 = MeanDay.getInstance();
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        int hour = date.getHours();
        for (int i = 0; i <= 24; i ++) {


            if (date.equals(mean2.getLocalDate())) { //stesso giorno


                if (mean2.getMap().get(hour) != null) {
                    Mean meanDay = mean2.getMap().get(hour);
                    meanDay.setSampleSum((float) speed);  // modificare con il valore della velocità
                    meanDay.setSampleSize();
                    mean2.getMap().put(hour, meanDay);
                } else {
                    Mean meanDay = new Mean();
                    meanDay.setSampleSum((float) speed); //modificare con il valore della velocità
                    meanDay.setSampleSize();
                    mean2.getMap().put(hour, meanDay);
                }
            } else {
                mean2.setLocalDate(date);
                mean2.getMap().clear();
                Mean meanDay = new Mean();
                meanDay.setSampleSum((float) speed); // modificare con il valore della velocità
                meanDay.setSampleSize();
                mean2.getMap().put(hour, meanDay);
            }
            Log.e("c", "Programma per " + i + " eseguito in ora " + hour + " giorno");

        }

        //END TRY
    }

    private void checkSpeedAndAcceleration(Location location) {
        if (location.hasSpeed()) {
            if (initialSpeed == -1) {
                //first time that velocity has been detected, I don't check acceleration
                initialSpeed = (int) ((location.getSpeed()*3600)/1000);
                initialTime = System.currentTimeMillis();
                Log.e("SPEED", "Speed: " + initialSpeed);
                updateStatistics(initialSpeed);
                //Toast.makeText(context, "Speed: " + initialSpeed, Toast.LENGTH_SHORT).show();
                //Check speed limits through OpenStreetMap
                //speedLimitManager.requestSpeedLimit("www.overpass-api.de/api/xapi?*[maxspeed=*][bbox=5.6283473,50.5348043,5.6285261,50.534884]\n");
                //TODO
                //check speed limits
            } else {
                speed = (int) ((location.getSpeed()*3600)/1000);
                time = System.currentTimeMillis();
                deltaT = (time - initialTime)/1000;
                double acceleration = ((double) (speed - initialSpeed)) / deltaT;
                //Toast.makeText(context, "Acceleration: " + acceleration, Toast.LENGTH_SHORT).show();
                //Toast.makeText(context, "Speed: " + speed, Toast.LENGTH_SHORT).show();
                Log.e("SPEED", "Speed: " + speed);
                Log.e("ACCELERATION", "Acceleration: " + acceleration);
                updateStatistics(speed);
                //TODO
                //check speed limits or abrupt braking or acceleration
                //speedLimitManager.requestSpeedLimit("www.overpass-api.de/api/xapi?*[maxspeed=*][bbox=5.6283473,50.5348043,5.6285261,50.534884]\n");
                initialTime = time;
                initialSpeed = speed;
            }
        }
    }



    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (user == null || user.getUid().isEmpty()) {
                return;
            }

            checkSpeedAndAcceleration(location);

            user.getMtxUpdatePosition().lock();
            String oldCountry = user.getCountry();
            String oldRegion = user.getRegion();
            String oldSubRegion = user.getSubRegion();
            user.getMtxUpdatePosition().unlock();

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            // TODO: se è troppo dispendioso utilizza anche getLocationFromCoordinates(lat, long, 1)
            final String[] strings = getLocationFromCoordinates(latitude, longitude);

            // Update SingletonUser state
            user.setLatitude(latitude);
            user.setLongitude(longitude);
            user.setCountry(strings[0]);
            user.setRegion(strings[1]);
            user.setSubRegion(strings[2]);
            Log.d(TAG, "Position: " + user.getCountry() + " / " + user.getRegion() + " / " + user.getSubRegion());

            if (!oldSubRegion.equals(user.getSubRegion()) ||
                    !oldRegion.equals(user.getRegion()) ||
                    !oldCountry.equals(user.getCountry())) {
                // Remove user from position node only if his position has changed
                FirebaseDatabaseManager.removeOldPosition(new String[] {oldCountry, oldRegion, oldSubRegion});
                // Create user in new position
                FirebaseDatabaseManager.createNewUserPosition();
            }

            if (user.getSubRegion().equals(SUB_REGION) ||
                    user.getRegion().equals(REGION) ||
                    user.getCountry().equals(COUNTRY)) {
                user.setAvailability(UNAVAILABLE);
            } else {
                user.setAvailability(AVAILABLE);
            }

            // Update position node
            FirebaseDatabaseManager.updatePositionCoordAndAvail();
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
    };

    public boolean isGPSEnabled() {
        return this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void updatePosition() {
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

        this.listenerSetted = true;
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 50, this.locationListener);
    }

    public void removeLocationUpdates() {
        this.locationManager.removeUpdates(this.locationListener);
        this.listenerSetted = false;
    }

    public boolean isListenerSetted() {
        return this.listenerSetted;
    }

    /**
     * Get Location from coordinates
     * @param latitude latitude
     * @param longitude longitude
     * @param maxResult result's number
     * @return string[0] --> Nation; string[1] --> Region; string[2] --> District
     */
    public static String[] getLocationFromCoordinates(double latitude, double longitude, int maxResult) {
        final String[] strings = new String[3];
        try {
            /*
                The issue is Geocoder backend service is not available in emulator.
                Emulator needs Google API in order to works correctly with Geocoder.
             */
            List<Address> addresses = PositionManager.geocoder.getFromLocation(latitude, longitude, maxResult);

            if (addresses.size() == 0) {
                Thread geoC = new Thread(new RetrieveAndParseJSONPosition(new RetrieveAndParseJSONPosition.CallbackRetrieveAndParseJSON() {
                    @Override
                    public void onDataComputed(String[] position) {
                        if (position != null) {
                            strings[0] = position[0];
                            strings[1] = position[1];
                            strings[2] = position[2];
                        } else {
                            strings[0] = user.getCountry();
                            strings[1] = user.getRegion();
                            strings[2] = user.getSubRegion();
                        }
                    }
                }, latitude, longitude));
                geoC.start();
                geoC.join();
                return strings;
            } else {
                strings[0] = addresses.get(0).getCountryName();
                strings[1] = addresses.get(0).getAdminArea();
                strings[2] = addresses.get(0).getSubAdminArea();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();

            strings[0] = COUNTRY;
            strings[1] = REGION;
            strings[2] = SUB_REGION;
        }
        return strings;
    }

    public static String[] getLocationFromCoordinates(double latitude, double longitude) {
        final String[] strings = new String[3];
        try {
            Thread geoC = new Thread(new RetrieveAndParseJSONPosition(new RetrieveAndParseJSONPosition.CallbackRetrieveAndParseJSON() {
                @Override
                public void onDataComputed(String[] position) {
                    if (position != null) {
                        strings[0] = position[0];
                        strings[1] = position[1];
                        strings[2] = position[2];
                    } else {
                        strings[0] = user.getCountry();
                        strings[1] = user.getRegion();
                        strings[2] = user.getSubRegion();
                    }
                }
            }, latitude, longitude));
            geoC.start();
            geoC.join();
        } catch (InterruptedException e) {
            e.printStackTrace();

            strings[0] = COUNTRY;
            strings[1] = REGION;
            strings[2] = SUB_REGION;
        }
        return strings;
    }
}