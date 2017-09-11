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
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by matti on 28/08/2017.
 */

public class PositionManager
        extends Application
        implements Constants {

    private final static String TAG = PositionManager.class.getSimpleName();

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private double latitude = 0;
    private double longitude = 0;
    private double oldLatitude, oldLongitude;
    private String oldCountry = OLD_COUNTRY;
    String oldRegion = OLD_REGION;
    String oldSubRegion = OLD_SUB_REGION;
    private String country, region, subRegion;
    private static PositionManager positionManager;
    private Activity activity;
    private String userId, email;
    private Geocoder geocoder;
    private FirebaseDatabase database;
    private Map<String, Object> emailMap;
    Location oldLocation;
    private DatabaseReference myRef;


    //Singleton
    private PositionManager(Activity activity) {
        this.activity = activity;
        geocoder = new Geocoder(activity.getApplicationContext(), Locale.ENGLISH);
        database = FirebaseDatabase.getInstance();
        deleteLastPosition();
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public boolean isGPSEnabled() {
        final LocationManager manager = (LocationManager) this.activity.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
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
            Log.d("DB", "PERMISSION GRANTED");
        }

        userId = SingletonUser.getInstance().getUid();
        email = SingletonUser.getInstance().getEmail();

        //PENDING OLD LOCATION
        /*
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
                    oldSubRegion = oldAddresses.get(0).getSubAdminArea();
                }
                if (oldCountry==null)
                    oldCountry = OLD_COUNTRY;
                if (oldSubRegion==null)
                    oldSubRegion = OLD_SUB_REGION;
                if (oldRegion==null)
                    oldRegion = OLD_REGION;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            oldCountry = OLD_COUNTRY;
            oldRegion = OLD_REGION;
            oldSubRegion = OLD_SUB_REGION;
        }
        */
        //PENDING OLD LOCATION END


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Map<String, Object> coordinates = new ArrayMap<>();
                emailMap = new ArrayMap<>();
                coordinates.put(CHILD_CURRENT_POSITION, latitude+";"+longitude);
                emailMap.put(CHILD_EMAIL, email);

                final String[] strings = getLocationFromCoordinates(latitude, longitude, 1);
                country = strings[0];
                region = strings[1];
                subRegion = strings[2];

                //coordinates.put("lat", latitude);
                //coordinates.put("lon", longitude);
                Boolean radicalChange = false;
                if (oldCountry != null && oldRegion != null && oldSubRegion != null) {
                    if (!oldCountry.equals(country) || !oldRegion.equals(region) || !oldSubRegion.equals(subRegion)) {
                        //delete the current user instance and create a newer
                        myRef = database.getReference(NODE_POSITION + "/" + oldCountry + "/" + oldRegion + "/" + oldSubRegion + "/" + userId);
                        myRef.removeValue();
                        radicalChange = true;
                    }
                }
                if (radicalChange) {
                    //I have to add old user information
                    radicalChange = false;
                }
                oldCountry = country;
                oldRegion = region;
                oldSubRegion = subRegion;

                Log.d(TAG, "Position: " + country + " / " + region + " / " + subRegion + " / " + userId);
                if (country == null || region == null ||
                        subRegion == null || userId == null) {
                    return;
                }
                // TODO to fix: com.google.firebase.database.DatabaseException: Invalid Firebase Database path: Division No. 17. Firebase Database paths must not contain '.', '#', '$', '[', or ']'
                myRef = DatabaseManager.getDatabaseReference()
                        .child(NODE_POSITION)
                        .child(country)
                        .child(region)
                        .child(subRegion)
                        .child(userId);

                // we need to update even "users" node
                DatabaseManager.setDataOnLocationChange(new double[] {latitude, longitude});
                ////

                final Map<String, Object> currentCoordinates = coordinates;
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (SingletonUser.getInstance() == null) {
                            return;
                        }

                        if (!dataSnapshot.hasChild(CHILD_EMAIL)) {
                            myRef.updateChildren(emailMap);
                        }
                        // update to the last known position
                        if (!dataSnapshot.hasChild(CHILD_CURRENT_POSITION) ) {
                            myRef.updateChildren(currentCoordinates);
                        } else if (!dataSnapshot.child(CHILD_CURRENT_POSITION)
                                    .getValue()
                                    .toString()
                                    .equals(StringParser.getStringFromCoordinates(latitude, longitude))) {
                                DatabaseManager.updateCurrentPosition(new double[] {latitude, longitude}, strings);
                        }
                        ////
                        if (SingletonUser.getInstance().getPhotoUrl() != null &&
                                !dataSnapshot.hasChild(CHILD_IMAGE)) {
                            myRef.child(CHILD_IMAGE).setValue(
                                    SingletonUser.getInstance().getPhotoUrl().toString()
                            );
                        }

                        //update user availability
                        if (!dataSnapshot.hasChild(CHILD_AVAILABILITY)) {
                            myRef.child(CHILD_AVAILABILITY).setValue(AVAILABLE);
                            DatabaseManager.manageUserAvailability(AVAILABLE);
                        } else if (dataSnapshot.child(CHILD_AVAILABILITY).getValue().equals(UNAVAILABLE)) {
                            myRef.child(CHILD_AVAILABILITY).setValue(AVAILABLE);
                            DatabaseManager.manageUserAvailability(UNAVAILABLE);
                        }
                        ////

                        // update to latest point value
                        if (!dataSnapshot.hasChild(CHILD_POINTS)) {
                            myRef.child(CHILD_POINTS).setValue(
                                    SingletonUser.getInstance().getPoints()
                            );
                        } else if ((long) dataSnapshot.child(CHILD_POINTS).getValue() !=
                                SingletonUser.getInstance().getPoints()) {
                            DatabaseManager.updateCurrentPoint(SingletonUser.getInstance().getPoints(), strings);
                        }
                        ////
                        if (!dataSnapshot.hasChild(CHILD_USERNAME)) {
                            myRef.child(CHILD_USERNAME).setValue(
                                    SingletonUser.getInstance().getUsername() == null ?
                                            getString(R.string.username) : SingletonUser.getInstance().getUsername()
                            );
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

    public void deletePosition() {
        if (region != null && subRegion != null && country != null) {
            myRef = database.getReference("position" + "/" + country + "/" + region + "/" + subRegion + "/" + userId);
            myRef.removeValue();
        }
    }

    public void setUserUnavailable() {
        if (region != null && subRegion != null && country != null) {
            myRef = database.getReference(NODE_POSITION + "/" + country + "/" + region + "/" + subRegion + "/" + userId);
            Map<String, Object> availabilityMap = new ArrayMap<>();
            availabilityMap.put(CHILD_AVAILABILITY, UNAVAILABLE);
            myRef.updateChildren(availabilityMap);
            // Manage users node availability
            DatabaseManager.manageUserAvailability(UNAVAILABLE);
            ////
        }
    }

    public void setUserAvailable() {
        if (region != null && subRegion != null && country != null) {
            myRef = database.getReference(NODE_POSITION + "/" + country + "/" + region + "/" + subRegion + "/" + userId);
            Map<String, Object> availabilityMap = new ArrayMap<>();
            availabilityMap.put(CHILD_AVAILABILITY, AVAILABLE);
            myRef.updateChildren(availabilityMap);
            // Manage users node availability
            DatabaseManager.manageUserAvailability(AVAILABLE);
            ////
        }
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

    private void deleteLastPosition() {
        //SEARCH LAST POSITION
        if (SingletonUser.getInstance().getUid() != null) {
            myRef = DatabaseManager.getDatabaseReference()
                    .child(NODE_USERS)
                    .child(SingletonUser.getInstance().getUid());
            //GET LAST LATITUDE AND LONGITUDE IF EXIST
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(CHILD_CURRENT_POSITION)) {
                        String[] currentCoordinates = StringParser.getCoordinates(dataSnapshot.child(CHILD_CURRENT_POSITION).getValue().toString());
                        double currentLatitude = Double.parseDouble(currentCoordinates[0]);
                        double currentLongitude = Double.parseDouble(currentCoordinates[1]);
                        List<Address> addresses = null;
                        String countryName = COUNTRY;
                        String regionName = REGION;
                        String subRegionName = SUB_REGION;
                        try {
                            addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
                            if (addresses.size()!=0) {
                                if (addresses.get(0).getCountryName() != null && addresses.get(0).getAdminArea() != null &&
                                addresses.get(0).getSubAdminArea()!= null) {
                                    countryName = addresses.get(0).getCountryName();
                                    regionName = addresses.get(0).getAdminArea();
                                    subRegionName = addresses.get(0).getSubAdminArea();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //DELETE POSITION
                        myRef = DatabaseManager.getDatabaseReference()
                                .child(NODE_POSITION)
                                .child(countryName)
                                .child(regionName)
                                .child(subRegionName);
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(SingletonUser.getInstance().getUid())) {
                                    myRef.child(SingletonUser.getInstance().getUid()).removeValue();
                                    updatePosition();
                                } else {
                                    updatePosition();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        updatePosition();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            updatePosition();
        }
    }
}