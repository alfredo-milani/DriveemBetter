package com.driveembetter.proevolutionsoftware.driveembetter.utils;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.Mean;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.driveembetter.proevolutionsoftware.driveembetter.threads.RetrieveAndParseJSONPosition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by matti on 28/08/2017.
 */

public class PositionManager
        implements Constants {

    private final static String TAG = PositionManager.class.getSimpleName();

    private static PositionManager positionManager;
    private static Geocoder geocoder;
    private LocationManager locationManager;
    private boolean listenerSetted = false;
    private Context context;
    private double initialTime;
    private double time;
    private float initialSpeed; //Symbolic value just to check when "onLocationChanged" is called for the first time
    private double deltaT;
    private Response response;
    private String lastPosition;
    private static boolean statisticsToPush;
    TextToSpeech tts;


    // Singleton
    private PositionManager(Context context) {
        this.context = context;
        this.lastPosition = "";
        PositionManager.geocoder = new Geocoder(context, Locale.ITALIAN);
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        PositionManager.statisticsToPush = false;
        initialSpeed = -1;
        this.updatePosition();
    }

    public static PositionManager getInstance(Context context) {
        if (positionManager == null) {
            positionManager = new PositionManager(context);
        }

        return positionManager;
    }

    private void updateStatistics(double speed, double acceleration) {

        PositionManager.setStatisticsToPush(true);

        // TODO weekly updates

        //TRY
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        int hour = date.getHours();
        for (int i = 0; i < 24; i ++) {
            SingletonUser user = SingletonUser.getInstance();
            if (user == null) {
                Log.e(TAG, "user NULL: no updated statistics");
                return;
            }
            if (date.equals(user.getMeanDay().getLocalDate())) { //stesso giorno
                if (user.getMeanDay().getMap().get(hour) != null) {
                    Mean meanDay = user.getMeanDay().getMap().get(hour);
                    meanDay.setSampleSumVelocity((float) speed);
                    meanDay.setSampleSizeVelocity();
                    meanDay.setSampleSumAcceleration((float) acceleration);
                    meanDay.setSampleSizeAcceleration();
                    user.getMeanDay().getMap().put(hour, meanDay);
                } else {
                    Mean meanDay = new Mean();
                    meanDay.setSampleSumVelocity((float) speed);
                    meanDay.setSampleSizeVelocity();
                    meanDay.setSampleSumAcceleration((float) acceleration);
                    meanDay.setSampleSizeAcceleration();
                    user.getMeanDay().getMap().put(hour, meanDay);
                }
            } else {
                user.getMeanDay().setLocalDate(date);
                user.getMeanDay().getMap().clear();
                Mean meanDay = new Mean();
                meanDay.setSampleSumVelocity((float) speed); // modificare con il valore della velocità
                meanDay.setSampleSizeVelocity();
                meanDay.setSampleSumVelocity((float) acceleration);
                meanDay.setSampleSizeAcceleration();
                user.getMeanDay().getMap().put(hour, meanDay);
            }
            // Log.e("c", "Programma per " + i + " eseguito in ora " + hour + " giorno");
        }
        //END TRY
    }


    private void checkSpeedAndAcceleration(Location location) throws IOException {

        String currentPosition = "";
        List<Address> addresses;
        // TODO Utilizzare lo stato corrente di SingletonUser al posto di utilizzare il geocoder
        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
        if(addresses != null && addresses.size() > 0 ){
            Address address = addresses.get(0);
            // Thoroughfare seems to be the street name without numbers
            currentPosition = address.getThoroughfare();
        }

        if (location.hasSpeed()) {
            if (initialSpeed == -1) {
                //first time that velocity has been detected, I don't check acceleration
                initialSpeed = (int) ((location.getSpeed() * 3600) / 1000);
                initialTime = System.currentTimeMillis();
                Log.e("SPEED", "Speed: " + initialSpeed);
                updateStatistics(initialSpeed, 0);
                //TODO
                //check speed limits
                SpeedLimitManager speedLimitManager = new SpeedLimitManager();
                speedLimitManager.execute(String.valueOf(initialSpeed), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
            } else {
                float speed = ((location.getSpeed() * 3600) / 1000);
                time = System.currentTimeMillis();
                deltaT = (time - initialTime) / 1000;
                double acceleration = ((double) (speed - initialSpeed)) / deltaT;

                updateStatistics(speed, acceleration);

                //TODO ??
                //check speed limits or abrupt braking or acceleration
                if (acceleration > 5)
                    alertAcceleration(1);
                if (acceleration < -8)
                    alertAcceleration(-1);
                if (currentPosition != null && lastPosition != null &&
                        !currentPosition.equals(lastPosition)) {
                    SpeedLimitManager speedLimitManager = new SpeedLimitManager();
                    speedLimitManager.execute(String.valueOf(speed), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                }
                initialTime = time;
                initialSpeed = speed;
                lastPosition = currentPosition;
            }
        }
    }



    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            SingletonUser user = SingletonUser.getInstance();
            if (user != null && (user.getMtxUpdatePosition().isLocked() ||
                    user.getUid().isEmpty())) {
                // Al posto di prendere lock appensantendo il workflow skip se è in corso il processo di aggiornameto dei dati della posizione
                Log.d(TAG, "Aggiornamento ultima posizione nota da Firebase. Skipping onLocationChanged");;
                return;
            }

            try {
                checkSpeedAndAcceleration(location);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String oldCountry = user.getCountry();
            String oldRegion = user.getRegion();
            String oldSubRegion = user.getSubRegion();
            Log.d(TAG, "PositionOLD: " + oldCountry + " / " + oldRegion + " / " + oldSubRegion);

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            final String[] strings = getLocationFromCoordinates(latitude, longitude, 1);

            // Update SingletonUser state
            user.setLatitude(latitude);
            user.setLongitude(longitude);

            // Update zona only if all three areas are not null
            if (strings[0] != null && strings[1] != null && strings[2] != null) {
                user.setCountry(strings[0]);
                user.setRegion(strings[1]);
                user.setSubRegion(strings[2]);
                Log.d(TAG, "PositionNEW: " + user.getCountry() + " / " + user.getRegion() + " / " + user.getSubRegion());

                if (!oldSubRegion.equals(user.getSubRegion()) ||
                        !oldRegion.equals(user.getRegion()) ||
                        !oldCountry.equals(user.getCountry())) {
                    Log.d(TAG, "Removing old position");
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

            Log.d(TAG, "Country: " + strings[0] + " Region: " + strings[1] + " SubRegion: " + strings[2]);
            if (addresses == null || strings[0] == null ||
                    strings[1] == null || strings[2] == null) {
                Thread geoC = new Thread(new RetrieveAndParseJSONPosition(new RetrieveAndParseJSONPosition.CallbackRetrieveAndParseJSON() {
                    @Override
                    public void onDataComputed(String[] position) {
                        if (position != null) {
                            strings[0] = position[0];
                            strings[1] = position[1];
                            strings[2] = position[2];
                        } else {
                            strings[0] = SingletonUser.getInstance().getCountry();
                            strings[1] = SingletonUser.getInstance().getRegion();
                            strings[2] = SingletonUser.getInstance().getSubRegion();
                        }
                    }
                }, latitude, longitude));
                geoC.start();
                geoC.join();
                Log.d(TAG, "Country: " + strings[0] + " Region: " + strings[1] + " SubRegion: " + strings[2]);
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
                        strings[0] = SingletonUser.getInstance().getCountry();
                        strings[1] = SingletonUser.getInstance().getRegion();
                        strings[2] = SingletonUser.getInstance().getSubRegion();
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

    public float getInitialSpeed() {
        return this.initialSpeed;
    }

    public synchronized static boolean isStatisticsToPush() {
        return PositionManager.statisticsToPush;
    }

    public synchronized static void setStatisticsToPush(boolean statisticsToPush) {
        PositionManager.statisticsToPush = statisticsToPush;
    }

    //ADDED BY PONZINO_THE_WOLF_94
    private class SpeedLimitManager extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            String latitude = strings[1];
            String longitude = strings[2];
            Log.e("DEBUG", latitude +  "   " + longitude);
            String testLat = "41.850884";
            String testLong = "12.603843";


            //TEST



            //ENDTEST

            //TODO if I had to use google maps api to find speed limits, I would have to create the following url
            //https://roads.googleapis.com/v1/speedLimits?placeId=ChIJX12duJAwGQ0Ra0d4Oi4jOGE&placeId=ChIJLQcticc0GQ0RoiNZJVa5GxU&placeId=ChIJJ4vQRudkJA0RpednU70A-5M&key=YOUR_API_KEY
            //or
            //https://roads.googleapis.com/v1/speedLimits?path=38.75807927603043,-9.03741754643809|38.6896537,-9.1770515|41.1399289,-8.6094075&key=YOUR_API_KEY
            //API KEY AIzaSyC2TCyPFqoATPFy7pa7iKdefrpjPWqXJTc

            Request request = new Request.Builder()
                    //.url("http://www.overpass-api.de/api/xapi?*[bbox=5.6283473,50.5348043,5.6285261,50.534884][maxspeed=*]")
                    .url("http://www.overpass-api.de/api/interpreter?data=[out:json];way (around:20, " + latitude + ", " + longitude + ")[\"maxspeed\"];(  ._;  node(w););out;")
                    //["highway"];(  ._;  node(w););out;
                    .build();
            /*
            Request request = new Request.Builder()
                    .url("https://roads.googleapis.com/v1/speedLimits?path=" + latitude + "," + longitude + "&key=" + "AIzaSyC2TCyPFqoATPFy7pa7iKdefrpjPWqXJTc")
                    .build();
               */
            try {
                String maxSpeed = "";
                response = client.newCall(request).execute();
                String jsonData = response.body().string();

                //TEST
                Log.e("DEBUG", jsonData);

                JSONObject Jobject = new JSONObject(jsonData);
                JSONArray elements = Jobject.getJSONArray("elements");
                //OPEN STREET MAP MAY NOT FIND MAX SPEED OF A GIVEN AREA
                //MOREOVER, NODE 'elements' MAY BE EMPTY
                for (int i = 0; i < elements.length(); i++) {
                    JSONObject tempObj = elements.getJSONObject(i);
                    if (tempObj.has("tags")) {
                        JSONObject tags = tempObj.getJSONObject("tags");
                        if (tags.has("maxspeed")) {
                            maxSpeed = tags.getString("maxspeed");
                            String currentSpeed = strings[0];
                            Double speed = Double.parseDouble(currentSpeed);
                            Double mSpeed = Double.parseDouble(maxSpeed);
                            Log.e("DEBUG", "MAX SPEED: " + maxSpeed + " CURRENT SPEED: " + speed);
                            //TODO
                            if (speed > mSpeed) {
                                //Alert driver
                                alertSpeed(mSpeed);
                            }
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private void alertSpeed(final Double maxSpeed) {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR) {
                    if (Locale.getDefault().getDisplayLanguage().equals(Locale.UK) || Locale.getDefault().getDisplayLanguage().equals(Locale.US)) {
                        tts.setLanguage(Locale.UK);
                        tts.speak("You're going too fast, the speed limit is " + maxSpeed + " kilometers per hour", TextToSpeech.QUEUE_ADD, null);
                    }
                    else {
                        tts.setLanguage(Locale.ITALY);
                        tts.speak("Stai correndo troppo, il limite di velocità è di " + maxSpeed + " chilometri orari", TextToSpeech.QUEUE_ADD, null);
                    }
                }
            }

        });
    }

    private void alertAcceleration(final int type) {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR) {
                    if (Locale.getDefault().getDisplayLanguage().equals(Locale.UK) || Locale.getDefault().getDisplayLanguage().equals(Locale.US)) {
                        tts.setLanguage(Locale.UK);
                        if (type == 1)
                            tts.speak(ENGLISH_ACCELERATION_ALERT, TextToSpeech.QUEUE_ADD, null);
                        else
                            tts.speak(ENGLISH_BRAKING_ALERT, TextToSpeech.QUEUE_ADD, null);
                    } else {
                        tts.setLanguage(Locale.ITALY);
                        if (type == 1)
                            tts.speak(ITALIAN_ACCELERATION_ALERT, TextToSpeech.QUEUE_ADD, null);
                        else
                            tts.speak(ITALIAN_BRAKING_ALERT, TextToSpeech.QUEUE_ADD, null);
                    }
                }

            }

        });
    }
    //END ADDED BY FROM PONZINO_THE_WOLF_94
}