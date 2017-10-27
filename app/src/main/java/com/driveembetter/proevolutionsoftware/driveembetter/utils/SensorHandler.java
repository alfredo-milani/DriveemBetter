package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity.EmergencyActivity;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CHILD_FIRST_FRIEND;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CHILD_SECOND_FRIEND;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.NODE_USERS;
import static com.driveembetter.proevolutionsoftware.driveembetter.utils.PointManager.g;

/**
 * Created by matti on 31/08/2017.
 */

public class SensorHandler extends Activity
        implements SensorEventListener {

    private final static String TAG = SensorHandler.class.getSimpleName();

    private final static int ACC_THRESHOLD = 3;

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



    public SensorHandler(Activity activity) {
        this.activity = activity;
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
                Log.e("DEBUG", "SENSOR SIZE OK");
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
                if (acceleration >= ACC_THRESHOLD) {
                    crashDetected = true;
                    new TimeRestorer().execute();
                    final DatabaseReference databaseReference = FirebaseDatabaseManager.getDatabaseReference()
                            .child(NODE_USERS)
                            .child(SingletonUser.getInstance().getUid());
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChild(CHILD_FIRST_FRIEND) && !dataSnapshot.hasChild(CHILD_SECOND_FRIEND)) {
                                Toast.makeText(activity, "You don't have any trusted friends selected", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent emergencyIntent = new Intent(activity.getApplicationContext(), EmergencyActivity.class);
                                /*
                                emergencyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                emergencyIntent.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED +
                                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD +
                                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON +
                                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                                        */
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
