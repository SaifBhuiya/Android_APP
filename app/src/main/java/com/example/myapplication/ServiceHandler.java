package com.example.myapplication;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ServiceHandler extends Worker {
    private SensorManager sensorManager;
    private Sensor gyroSensor, lightSensor, accelSensor, proxiSensor;
    private DbHelper dbHelper;
    private float current_light, current_proximity, current_accelerometer_x, current_accelerometer_y, current_accelerometer_z, current_gyroscope_x, current_gyroscope_y, current_gyroscope_z;

    public ServiceHandler(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        dbHelper = new DbHelper(context);

        // Sensor Manager
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            proxiSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        if (sensorManager == null) {
            return Result.failure();
        }

        SensorEventListener listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                    current_gyroscope_x = event.values[0];
                    current_gyroscope_y = event.values[1];
                    current_gyroscope_z = event.values[2];
                }
                if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                    current_proximity = event.values[0];
                }
                if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                    current_light = event.values[0];
                }
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    current_accelerometer_x = event.values[0];
                    current_accelerometer_y = event.values[1];
                    current_accelerometer_z = event.values[2];
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

        // Register sensors
        sensorManager.registerListener(listener, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, proxiSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);

        try {
            Thread.sleep(2000); // Allow time for sensors to collect data
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Unregister the listener after collecting data
        sensorManager.unregisterListener(listener);

        // Store data in SQLite
        dbHelper.insertSensorData(
                current_light, current_proximity,
                current_accelerometer_x, current_accelerometer_y, current_accelerometer_z,
                current_gyroscope_x, current_gyroscope_y, current_gyroscope_z
        );

        return Result.success();
    }
}