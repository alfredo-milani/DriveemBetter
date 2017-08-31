package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import android.app.Activity;
import android.app.Application;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import static android.hardware.SensorManager.getAltitude;

/**
 * Created by matti on 31/08/2017.
 */

public class SensorHandler extends Application implements SensorEventListener {
    private int speed;
    private SensorManager sensorManager;
    private List<Sensor> sensors;
    private Sensor barometer;
    private Activity activity;
    float lastX = 0;    //
    float lastY = 0;   //
    float lastZ = 0;  //
    float deltaX, deltaY, deltaZ;
    double deltaAcceleration;
    float pressure, altitude;
    double currentV;

    public void startSensorHandler(Activity activity) {
        this.activity = activity;
        sensorManager=(SensorManager) activity.getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensors = sensorManager.getSensorList(Sensor.TYPE_PRESSURE);
        if(sensors.size() > 0) {
            Log.e("DEBUG", "SENSOR SIZE OK");
            barometer = sensors.get(0);
            sensorManager.registerListener(this, barometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            //I don't use Math.abs so I can check whenever the acceleration is positive or negative
            deltaX = lastX - event.values[0];
            deltaY = lastY - event.values[1];
            deltaZ = lastZ - event.values[2];

            //if the change is below 2, it is just plain noise
            if (Math.abs(deltaX) < 2)
                deltaX = 0;
            if (Math.abs(deltaY) < 2)
                deltaY = 0;
            if (Math.abs(deltaZ) < 2)
                deltaZ = 0;

            //Macheroni Solution
            if (Math.abs(deltaX) >= 2 || Math.abs(deltaY) >= 2 || Math.abs(deltaZ) >= 2) {
                deltaAcceleration = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2) + Math.pow(deltaZ, 2));
                Log.e("ACCELERATION", "X: " + deltaX + " Y: " + deltaY + " Z: " + deltaZ);
                //Log.e("ACCELERATION", "Acceleration: " + deltaAcceleration);
                Toast.makeText(activity, "Acceleration: " + "X: " + deltaX + " Y: " + deltaY + " Z: " + deltaZ, Toast.LENGTH_LONG).show();
                //Toast.makeText(activity, "Acceleration: " + deltaAcceleration, Toast.LENGTH_SHORT).show();
            }
            //TODO
            //check suspected brakes or suspected accelerations
            lastX=event.values[0];
            lastY=event.values[1];
            lastZ=event.values[2];
        }
        if (event.sensor.getType()==Sensor.TYPE_PRESSURE) {
            pressure = event.values[0];
            altitude = getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);
            //TODO
            //send altitude to RabbitMQ
            Toast.makeText(activity, "Altitude: " + altitude, Toast.LENGTH_SHORT).show();
            Log.e("ALTITUDE", "Altitude: " + altitude);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
