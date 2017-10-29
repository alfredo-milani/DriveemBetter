package com.driveembetter.proevolutionsoftware.driveembetter.utils;


import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.Mean;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.MeanWeek;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.driveembetter.proevolutionsoftware.driveembetter.threads.RetrieveAndParseJSONPosition;
import com.driveembetter.proevolutionsoftware.driveembetter.threads.YahooWeatherParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.driveembetter.proevolutionsoftware.driveembetter.utils.PointManager.FIRST_ACCELERATION_BOUND;
import static com.driveembetter.proevolutionsoftware.driveembetter.utils.PointManager.FIRST_DECELERATION_BOUND;

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
    private Activity activity;
    private double initialTime;
    private static float initialSpeed; //Symbolic value just to check when "onLocationChanged" is called for the first time
    private String lastPosition;
    private Response response;
    private static boolean statisticsToPush;
    private TextToSpeech tts;
    private Speedometer speedometer;
    private ImageView speedLimitSign, weatherIcon;
    private TextView speedLimitText, windText, windDirectionText, positionText, temperatureText, humidityText, visibilityText;
    private ProgressBar yahooProgressBar;


    // Singleton
    private PositionManager(Activity activity) {
        this.activity = activity;
        this.lastPosition = "";
        PositionManager.geocoder = new Geocoder(this.activity, Locale.ITALIAN);
        this.locationManager = (LocationManager) this.activity.getSystemService(Context.LOCATION_SERVICE);
        PositionManager.statisticsToPush = false;
        initTextToSpeech();

        initialSpeed = -1;
        this.updatePosition();
    }

    public static PositionManager getInstance(Activity activity) {
        if (positionManager == null) {
            positionManager = new PositionManager(activity);
        }

        return positionManager;
    }


    public void createTools(View view) {
        speedometer = view.findViewById(R.id.Speedometer);
        speedLimitSign = view.findViewById(R.id.speed_limit);
        speedLimitText = view.findViewById(R.id.speed_limit_text);
        weatherIcon = view.findViewById(R.id.weather_icon);
        windText = view.findViewById(R.id.wind_text);
        windDirectionText = view.findViewById(R.id.wind_direction_text);
        positionText = view.findViewById(R.id.position_text);
        temperatureText = view.findViewById(R.id.temperature);
        humidityText = view.findViewById(R.id.humidity_text);
        visibilityText = view.findViewById(R.id.visibility_text);
        yahooProgressBar = view.findViewById(R.id.yahoo_progress_bar);
    }

    public static void resetCity() {
        SingletonUser.getInstance().setCity(CITY);
    }

    private void updateStatistics(double speed, double acceleration) {
        SingletonUser user = SingletonUser.getInstance();
        if (user == null) {
            Log.e(TAG, "user NULL: no updated statistics");
            return;
        }

        PositionManager.setStatisticsToPush(true);
        Date date = new Date();   // given date
        Calendar calendarCurrent = GregorianCalendar.getInstance();
        calendarCurrent.setTime(date);
        long currentTimestamp = System.currentTimeMillis();

        // Daily update
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(new Date(System.currentTimeMillis()));
        cal2.setTime(new Date(user.getMeanDay().getTimestamp()));
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

        Date dateLastUpdate = new Date(user.getMeanDay().getTimestamp());
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(dateLastUpdate);
        int currentHour = calendarCurrent.get(Calendar.HOUR_OF_DAY);
        if (sameDay) { // stesso giorno
            if (user.getMeanDay().getMap().get(calendarCurrent.get(Calendar.HOUR_OF_DAY)) != null) { // stessa ora
                Mean meanDay = user.getMeanDay().getMap().get(currentHour);
                meanDay.setSampleSumVelocity((float) speed);
                meanDay.setSampleSizeVelocity();
                meanDay.setSampleSumAcceleration((float) acceleration);
                if (acceleration != 0) {
                    meanDay.setSampleSizeAcceleration();
                }
                user.getMeanDay().getMap().put(currentHour, meanDay);
            } else {
                Mean meanDay = new Mean();
                meanDay.setSampleSumVelocity((float) speed);
                meanDay.setSampleSizeVelocity();
                meanDay.setSampleSumAcceleration((float) acceleration);
                if (acceleration != 0) {
                    meanDay.setSampleSizeAcceleration();
                }
                user.getMeanDay().getMap().put(currentHour, meanDay);
            }
        } else {
            user.getMeanDay().setTimestamp(currentTimestamp);
            user.getMeanDay().clear();
            user.getMeanDay().setClearDay(true);
            Mean meanDay = new Mean();
            meanDay.setSampleSumVelocity((float) speed);
            meanDay.setSampleSizeVelocity();
            meanDay.setSampleSumVelocity((float) acceleration);
            if (acceleration != 0) {
                meanDay.setSampleSizeAcceleration();
            }
            user.getMeanDay().getMap().put(currentHour, meanDay);
        }
        user.getMeanDay().setTimestamp(System.currentTimeMillis());

        //Weekly update
        Calendar calendarLast = GregorianCalendar.getInstance();
        calendarLast.setTime(new Date(user.getMeanWeek().getTimestamp()));
        int currentWeek = calendarCurrent.get(Calendar.WEEK_OF_MONTH);
        int lastWeek = calendarLast.get(Calendar.WEEK_OF_MONTH);
        MeanWeek meanWeek2 = user.getMeanWeek();
        if (currentWeek == lastWeek) { // stessa settimana (del mese)
            if (meanWeek2.getMap().get(currentWeek) != null) {
                Mean meanDay = meanWeek2.getMap().get(currentWeek);
                meanDay.setSampleSumVelocity((float) speed);  // velocity
                meanDay.setSampleSizeVelocity();
                meanDay.setSampleSumAcceleration((float) acceleration); // acceleration
                if (acceleration != 0) {
                    meanDay.setSampleSizeAcceleration();
                }
            } else {
                Mean meanDay = new Mean();
                meanDay.setSampleSumVelocity((float) speed); // velocity
                meanDay.setSampleSizeVelocity();
                meanDay.setSampleSumAcceleration((float) acceleration); // acceleration
                if (acceleration != 0) {
                    meanDay.setSampleSizeAcceleration();
                }
                meanWeek2.getMap().put(currentWeek, meanDay);
            }
        } else {
            meanWeek2.setTimestamp(currentTimestamp);
            meanWeek2.clear();
            meanWeek2.setClearWeek(true);
            Mean meanDay = new Mean();
            meanDay.setSampleSumVelocity((float) speed); // velocity
            meanDay.setSampleSizeVelocity();
            meanDay.setSampleSumVelocity((float) acceleration); // acceleration
            if (acceleration != 0) {
                meanDay.setSampleSizeAcceleration();
            }
            meanWeek2.getMap().put(currentWeek, meanDay);
        }
        user.getMeanWeek().setTimestamp(System.currentTimeMillis());
    }

    private void checkSpeedAndAcceleration(Location location) throws IOException {
        SingletonUser user = SingletonUser.getInstance();
        if (user == null) {
            return;
        }

        String currentPosition = "";

        String[] currentAddress = user.getAddress().split(",");
        if (currentAddress.length > 0) {
            currentPosition = currentAddress[0];
        }


        /*

        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
        if(addresses != null && addresses.size() > 0 ) {
            Address address = addresses.get(0);
            // Thoroughfare seems to be the street name without numbers
            currentPosition = address.getThoroughfare();
        }
        */

        if (location.hasSpeed()) {
            if (location.getSpeed() * 3600 / 1000 > 20) {
                if (location.getSpeed() * 3600 / 1000 > 140) {
                    alertGeneralSpeed();
                    PointManager.updatePoints(4, 0, 0);
                }
                if (initialSpeed == -1) {
                    //first time that velocity has been detected, I don't check acceleration
                    initialSpeed = (int) ((location.getSpeed() * 3600) / 1000);
                    initialTime = System.currentTimeMillis();
                    updateStatistics(initialSpeed, 0);
                    if (speedometer != null)
                        speedometer.onSpeedChanged(initialSpeed);
                    //check speed limits
                    SpeedLimitManager speedLimitManager = new SpeedLimitManager();
                    speedLimitManager.execute(String.valueOf(initialSpeed), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                } else {
                    float speed = ((location.getSpeed() * 3600) / 1000);
                    double time = System.currentTimeMillis();
                    double deltaT = (time - initialTime) / 1000;
                    double acceleration = ((double) (speed - initialSpeed)) / deltaT;

                    updateStatistics(speed, acceleration);
                    if (speedometer != null)
                        speedometer.onSpeedChanged(speed);

                    //check speed limits or abrupt braking or acceleration

                    PointManager.updatePoints(1, acceleration, 0);

                    if (acceleration > FIRST_ACCELERATION_BOUND)
                        alertAcceleration(1);
                    if (acceleration < FIRST_DECELERATION_BOUND)
                        alertAcceleration(-1);
                    if (currentPosition != null && lastPosition != null &&
                            !currentPosition.equals(lastPosition)) {
                        SpeedLimitManager speedLimitManager = new SpeedLimitManager();
                        speedLimitManager.execute(
                                String.valueOf(speed),
                                String.valueOf(location.getLatitude()),
                                String.valueOf(location.getLongitude())
                        );
                    } else {
                        PointManager.updatePoints(0, speed, -1);
                    }
                    initialTime = time;
                    initialSpeed = speed;
                    lastPosition = currentPosition;
                }
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
                Log.d(TAG, "Aggiornamento ultima posizione nota da Firebase. Skipping onLocationChanged");
                ;
                return;
            } else if (user == null) {
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
            String oldCity = user.getCity();
            String oldAddress = user.getAddress();
            Log.d(TAG, "PositionOLD: " + oldCountry + " / " + oldRegion + " / " + oldSubRegion + " / " + oldCity + " / " + oldAddress);

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            final String[] strings = getLocationFromCoordinates(latitude, longitude, 1);

            // Update SingletonUser state
            user.setLatitude(latitude);
            user.setLongitude(longitude);

            // Update zona only if all three areas are not null
            if (strings[0] != null && strings[1] != null && strings[2] != null &&
                    strings[3] != null && strings[4] != null) {
                user.setCountry(strings[0]);
                user.setRegion(strings[1]);
                user.setSubRegion(strings[2]);
                user.setCity(strings[3]);
                user.setAddress(strings[4]);
                Log.d(TAG, "PositionNEW: " + user.getCountry() + " / " + user.getRegion() + " / " + user.getSubRegion() + " / " + user.getCity() + " / " + user.getAddress());


                if (!oldCity.equals(strings[3])) {
                    // if driver change city
                    // positionText.setText(user.getCity() + ", " + user.getSubRegion() + ", " +
                    // user.getRegion() + ", " + user.getCountry());
                    user.setCity(strings[3]);

                    if (yahooProgressBar != null) {
                        yahooProgressBar.setVisibility(View.VISIBLE);
                        yahooProgressBar.getIndeterminateDrawable().setColorFilter(
                                ContextCompat.getColor(activity, R.color.colorPrimaryDark),
                                android.graphics.PorterDuff.Mode.MULTIPLY
                        );
                    }

                    new YahooWeatherParser(
                            weatherIcon,
                            windText,
                            windDirectionText,
                            temperatureText,
                            humidityText,
                            visibilityText,
                            yahooProgressBar
                    ).execute(strings[3], user.getCountry());
                }
                if (!oldAddress.equals(strings[4]) && positionText != null) {
                    positionText.setText(strings[4]);
                    user.setAddress(strings[4]);
                }

                if (user.getSubRegion().equals(SUB_REGION) ||
                        user.getRegion().equals(REGION) ||
                        user.getCountry().equals(COUNTRY)) {
                    user.setAvailability(UNAVAILABLE);
                } else {
                    user.setAvailability(AVAILABLE);
                }

                if (!oldSubRegion.equals(user.getSubRegion()) ||
                        !oldRegion.equals(user.getRegion()) ||
                        !oldCountry.equals(user.getCountry())) {
                    Log.d(TAG, "Removing old position");
                    // Remove user from position node only if his position has changed
                    FirebaseDatabaseManager.removeOldPosition(new String[]{oldCountry, oldRegion, oldSubRegion});
                    // Create user in new position
                    FirebaseDatabaseManager.createNewUserPosition();
                    // Update users node
                    FirebaseDatabaseManager.updateUserZonaAndAvailability();
                    return;
                }
            }

            // Update position node
            FirebaseDatabaseManager.updatePositionCoordAndAvail();
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
        if (!PermissionManager.isAllowed(this.activity, PermissionManager.COARSE_LOCATION_MANIFEST) ||
                !PermissionManager.isAllowed(this.activity, PermissionManager.FINE_LOCATION_MANIFEST)) {
            PermissionManager.askForPermission(
                    this.activity,
                    new String[] {
                            PermissionManager.COARSE_LOCATION_MANIFEST,
                            PermissionManager.FINE_LOCATION_MANIFEST,
                            PermissionManager.WAKE_LOCK_MANIFEST,
                            PermissionManager.DISABLE_KEYGUARD_MANIFEST
                    },
                    PermissionManager.ASK_FOR_LOCATION_POS_MAN
            );
            return;
        }

        this.listenerSetted = true;
        try {
            this.locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    50,
                    this.locationListener
            );
        } catch (SecurityException e) {
            e.printStackTrace();
        }
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
        String[] strings = new String[5];
        try {
            /*
                The issue is Geocoder backend service is not available in emulator.
                Emulator needs Google API in order to works correctly with Geocoder.
             */
            List<Address> addresses = PositionManager.geocoder.getFromLocation(latitude, longitude, maxResult);
            if (addresses == null) {
                strings = PositionManager.startGeocoderJSON(latitude, longitude);
            } else {
                strings[0] = addresses.get(0).getCountryName();
                strings[1] = addresses.get(0).getAdminArea();
                strings[2] = addresses.get(0).getSubAdminArea();
                strings[3] = addresses.get(0).getLocality();
                strings[4] = addresses.get(0).getAddressLine(0);

                if (strings[0] == null || strings[1] == null || strings[2] == null ||
                        strings[3] == null || strings[4] == null) {
                    strings = PositionManager.startGeocoderJSON(latitude, longitude);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: 13/10/17 mandare messaggio alla UI (MainFragmentActivity) con SingletonFirebaseProvider per mostrare toast con scritto di riavviare il dispositivo

            strings[0] = COUNTRY;
            strings[1] = REGION;
            strings[2] = SUB_REGION;
            strings[3] = CITY;
            strings[4] = ADDRESS;
        }

        return strings;
    }

    private static String[] startGeocoderJSON(double latitude, double longitude) {
        final String[] strings = new String[5];

        try {
            Thread geoC = new Thread(new RetrieveAndParseJSONPosition(new RetrieveAndParseJSONPosition.CallbackRetrieveAndParseJSON() {
                @Override
                public void onDataComputed(String[] position) {
                    if (position != null) {
                        strings[0] = position[0];
                        strings[1] = position[1];
                        strings[2] = position[2];
                        strings[3] = position[3];
                        strings[4] = position[4];
                    } else {
                        strings[0] = SingletonUser.getInstance().getCountry();
                        strings[1] = SingletonUser.getInstance().getRegion();
                        strings[2] = SingletonUser.getInstance().getSubRegion();
                        strings[3] = SingletonUser.getInstance().getCity();
                        strings[4] = SingletonUser.getInstance().getAddress();
                    }
                }
            }, latitude, longitude));
            geoC.start();
            geoC.join();
            Log.d(TAG, "Country: " + strings[0] + " Region: " + strings[1] + " SubRegion: " + strings[2] + " City: " + strings[3] + " Address: " + strings[4]);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return strings;
    }

    public static float getInitialSpeed() {
        return PositionManager.initialSpeed;
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
            String maxSpeed = "N/A";

            try {
                response = client.newCall(request).execute();
                String jsonData = response.body().string();


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
                            try
                            {
                                Double mSpeed = Double.parseDouble(maxSpeed);
                                //TODO
                                if (speed > mSpeed) {
                                    //Alert driver
                                    alertSpeed(mSpeed);
                                    PointManager.updatePoints(0, speed, mSpeed);
                                    if (speedLimitText != null) {
                                        publishProgress("design");
                                    }
                                } else {
                                    publishProgress("designStatic");
                                }
                            }
                            catch(NumberFormatException e)
                            {
                                e.printStackTrace();
                                maxSpeed = "N/A";
                            }

                        } else {
                            maxSpeed = "N/A";
                        }
                    } else {
                        maxSpeed = "N/A";
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (maxSpeed.equals("N/A")) {
                String currentSpeed = strings[0];
                Double speed = Double.parseDouble(currentSpeed);
                PointManager.updatePoints(0, speed, -1);
                publishProgress("designStatic");
            }
            publishProgress(maxSpeed);

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (speedLimitText != null &&
                    !values[0].equals("design") &&
                    !values[0].equals("designStatic")) {
                speedLimitText.setText(values[0]);
            }
            if (speedLimitSign != null && values[0].equals("design")){
                AlphaAnimation blinkanimation= new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
                blinkanimation.setDuration(1000); // duration - half a second
                blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
                blinkanimation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
                blinkanimation.setRepeatMode(Animation.REVERSE);
                speedLimitSign.startAnimation(blinkanimation);
            }
            if (speedLimitSign != null && speedLimitSign.getAnimation() != null &&
                    values[0].equals("designStatic")){
                speedLimitSign.clearAnimation();
            }
        }
    }

    private void alertSpeed(final Double maxSpeed) {
        if (tts != null) {
            double speed = maxSpeed;
            int spd = (int) speed;
            tts.speak(activity.getResources().getString(R.string.speed_alert) + spd + activity.getResources().getString(R.string.kmh),
                    TextToSpeech.QUEUE_ADD, null);
        }
    }

    private void alertGeneralSpeed() {
        if (tts != null) {
            tts.speak(activity.getResources().getString(R.string.general_speed_alert),
                    TextToSpeech.QUEUE_ADD, null);
        }
    }

    private void alertAcceleration(final int type) {
        if (tts != null) {
            if (type == 1)
                tts.speak(activity.getResources().getString(R.string.acceleration_alert),
                        TextToSpeech.QUEUE_ADD, null);
            else
                tts.speak(activity.getResources().getString(R.string.braking_alert),
                        TextToSpeech.QUEUE_ADD, null);
        }

    }

    private void initTextToSpeech() {
        tts = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    if (Locale.getDefault().getLanguage().equals("en"))
                        tts.setLanguage(Locale.UK);
                    else
                        tts.setLanguage(Locale.ITALIAN);
                }
            }
        });
    }

    public void removeTextToSpeech() {
        if(this.tts != null) {
            this.tts.stop();
            this.tts.shutdown();
        }
    }


    public void alertCrash() {
        if (tts != null)
            tts.speak(activity.getResources().getString(R.string.crash_detected), TextToSpeech.QUEUE_ADD, null);
    }

    public void noFriendsAlert() {
        if (tts != null)
            tts.speak(activity.getResources().getString(R.string.no_friends_alert), TextToSpeech.QUEUE_ADD, null);
    }


}