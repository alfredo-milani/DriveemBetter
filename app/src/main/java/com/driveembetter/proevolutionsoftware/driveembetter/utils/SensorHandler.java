package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CHILD_FIRST_FRIEND;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CHILD_PHONE_NO;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.CHILD_SECOND_FRIEND;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.NODE_USERS;
import static com.driveembetter.proevolutionsoftware.driveembetter.utils.PointManager.g;

/**
 * Created by matti on 31/08/2017.
 */

public class SensorHandler extends Activity implements SensorEventListener {

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
        /*
        if (event.sensor.getType()==Sensor.TYPE_PRESSURE) {
            pressure = event.values[0];
            altitude = getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);
            //TODO
            //send altitude to RabbitMQ
            Log.d("ALTITUDE", "Altitude: " + altitude);
        }
        */
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            double acceleration = Math.sqrt((x*x) + (y*y) + (z*z))/g;
            if (!crashDetected) {
                if (acceleration >= 3) {
                    alertCrash();
                    Toast.makeText(activity, activity.getResources().getString(R.string.crash_detected), Toast.LENGTH_LONG).show();;
                    crashDetected = true;
                    new TimeRestorer().execute();

                    //start timer
                    final CountDownTimer myCountDownTimerObject = new CountDownTimer(30000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            if ((double) (millisUntilFinished / 1000) % 2 < 200) {
                                emergencyButton.setText("" + millisUntilFinished / 1000);
                            }
                        }

                        public void onFinish() {
                            if (!canceled) {
                                emergencyButton.setVisibility(View.GONE);
                                SingletonUser user = SingletonUser.getInstance();
                                final DatabaseReference databaseReference = FirebaseDatabaseManager.getDatabaseReference()
                                        .child(NODE_USERS)
                                        .child(user.getUid());
                                //CASE: FRIEND 1
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(CHILD_FIRST_FRIEND)) {
                                            SingletonUser user = SingletonUser.getInstance();
                                            String phoneNo = dataSnapshot.child(CHILD_FIRST_FRIEND).child(CHILD_PHONE_NO).getValue().toString();
                                /*sendSMS(phoneNo, activity.getResources().getString(R.string.help_request) +
                                        "http://maps.google.com/?q="+String.valueOf(user.getLatitude())+","+String.valueOf(user.getLongitude()) +
                                        "\n" + activity.getResources().getString(R.string.nearby_hospitals) +
                                        "http://maps.google.com/maps?q=hospital&mrt=yp&sll="+String.valueOf(user.getLatitude())+","+String.valueOf(user.getLongitude())+"&output=kml");
                            */
                                            sendSMS(phoneNo, activity.getResources().getString(R.string.help_request) +
                                                    user.getAddress());
                                        }

                                        if (dataSnapshot.hasChild(CHILD_SECOND_FRIEND)) {
                                            SingletonUser user = SingletonUser.getInstance();
                                            String phoneNo = dataSnapshot.child(CHILD_SECOND_FRIEND).child(CHILD_PHONE_NO).getValue().toString();
                                /*sendSMS(phoneNo, activity.getResources().getString(R.string.help_request) +
                                        "http://maps.google.com/?q="+String.valueOf(user.getLatitude())+","+String.valueOf(user.getLongitude()) +
                                        "\n" + activity.getResources().getString(R.string.nearby_hospitals) +
                                        "http://maps.google.com/maps?q=hospital&mrt=yp&sll="+String.valueOf(user.getLatitude())+","+String.valueOf(user.getLongitude())+"&output=kml");
                                */
                                            sendSMS(phoneNo, activity.getResources().getString(R.string.help_request) +
                                                    user.getAddress());
                                        }
                                        canceled = false;
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    };
                    myCountDownTimerObject.start();
                    emergencyButton.setVisibility(View.VISIBLE);
                    emergencyButton.setText("30");
                    emergencyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            canceled = true;
                            myCountDownTimerObject.cancel();
                            emergencyButton.setVisibility(View.GONE);
                        }
                    });

                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(activity, "Emergency SMS Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(activity,ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
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
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.SEND_SMS)){
            Toast.makeText(this,"Please, accept send sms permission.",Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.SEND_SMS}, 0);
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

    public void alertCrash() {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR) {
                    if (Locale.getDefault().getDisplayLanguage().equals(Locale.ENGLISH)) {
                        tts.setLanguage(Locale.UK);
                        tts.speak(getResources().getString(R.string.crash_detected), TextToSpeech.QUEUE_ADD, null);
                    }
                    else {
                        tts.setLanguage(Locale.ITALY);
                        tts.speak(getResources().getString(R.string.crash_detected), TextToSpeech.QUEUE_ADD, null);
                    }
                }
            }
        });
    }


}
