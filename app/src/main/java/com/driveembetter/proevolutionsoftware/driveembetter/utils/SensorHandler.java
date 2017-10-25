package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity.EmergencyActivity;
import com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity.MainFragmentActivity;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CHILD_FIRST_FRIEND;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CHILD_PHONE_NO;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CHILD_SECOND_FRIEND;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.NODE_USERS;
import static com.driveembetter.proevolutionsoftware.driveembetter.utils.PointManager.g;

/**
 * Created by matti on 31/08/2017.
 */

public class SensorHandler extends Activity
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

        if (!checkPermission())
            requestPermission();
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
                if (acceleration >= 30) {
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
                                emergencyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                emergencyIntent.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED +
                                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD +
                                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON +
                                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
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


    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this.activity, Manifest.permission.SEND_SMS)){
            Toast.makeText(this.activity, "Please, accept send sms permission.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this.activity, new String[]{Manifest.permission.SEND_SMS}, 0);
        }
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


    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(this, "Emergency SMS Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(this,ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}
