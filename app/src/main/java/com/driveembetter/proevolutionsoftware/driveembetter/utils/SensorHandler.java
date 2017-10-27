package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity.EmergencyActivity;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static android.content.Context.SENSOR_SERVICE;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CHILD_FIRST_FRIEND;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CHILD_SECOND_FRIEND;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.NODE_USERS;
import static com.driveembetter.proevolutionsoftware.driveembetter.utils.PointManager.g;

/**
 * Created by matti on 31/08/2017.
 */

public class SensorHandler
        implements SensorEventListener {

    private final static String TAG = SensorHandler.class.getSimpleName();

    private SensorManager sensorManager;
    private List<Sensor> sensors;
    private Sensor barometer;
    private Activity activity;
    private Context context;
    private Boolean crashDetected = false;
    float pressure, altitude;
    private Button emergencyButton;
    private TextToSpeech tts;
    private Boolean canceled = false;
    private PowerManager.WakeLock wl;



    public SensorHandler(Activity activity) {
        this.activity = activity;
        final PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wl.acquire();
    }

    public void setEmergencyButton(Button emergencyButton) {
        this.emergencyButton = emergencyButton;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void startSensorHandler() {
        if (this.activity != null) {
            sensorManager=(SensorManager) activity.getSystemService(SENSOR_SERVICE);
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_FASTEST);
            sensors = sensorManager.getSensorList(Sensor.TYPE_PRESSURE);
            if(sensors.size() > 0) {
                barometer = sensors.get(0);
                sensorManager.registerListener(this, barometer, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }

    }

    public void removeSensorHandler() {
        if (this.sensorManager != null) {
            this.sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            double acceleration = Math.sqrt((x*x) + (y*y) + (z*z))/g;
            if (!crashDetected) {
                if (acceleration >= 3) {
                    Log.e("DEBUG", "ACCELERATION WORKING?");
                    crashDetected = true;
                    new TimeRestorer().execute();
                    final DatabaseReference databaseReference = FirebaseDatabaseManager.getDatabaseReference()
                            .child(NODE_USERS)
                            .child(SingletonUser.getInstance().getUid());
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChild(CHILD_FIRST_FRIEND) && !dataSnapshot.hasChild(CHILD_SECOND_FRIEND)) {
                                PositionManager.getInstance(activity).noFriendsAlert();
                                Toast.makeText(activity, context.getResources().getString(R.string.no_friends_alert), Toast.LENGTH_SHORT).show();
                            } else {
                                PositionManager.getInstance(activity).alertCrash();
                                Intent emergencyIntent = new Intent(activity.getApplicationContext(), EmergencyActivity.class);
                                activity.startActivity(emergencyIntent);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public class TimeRestorer extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                Thread.sleep(180000);
                crashDetected = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
