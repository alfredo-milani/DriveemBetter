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
    private SensorManager sensorManager;
    private List<Sensor> sensors;
    private Sensor barometer;
    private Activity activity;
    float pressure, altitude;

    public void startSensorHandler(Activity activity) {
        this.activity = activity;
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

    @Override
    public void onSensorChanged(SensorEvent event) {

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
