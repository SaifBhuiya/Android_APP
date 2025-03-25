package com.example.myapplication;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.Timer;
import java.util.TimerTask;

public class Foreground extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gyroSensor, lightSensor, accelSensor, proxiSensor;
    private float current_light, current_proximity,current_accelerometer_x,current_accelerometer_y,current_accelerometer_z,current_gyroscope_x,current_gyroscope_y,current_gyroscope_z;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private static final int NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID = "Foreground Service";

    public void onCreate() {
        super.onCreate();

        // Initialize sensor manager and sensors as you already have
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        proxiSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        registerSensor(gyroSensor);
        registerSensor(proxiSensor);
        registerSensor(lightSensor);
        registerSensor(accelSensor);

        // Create notification channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID,
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        } else {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }

        // Initialize the notification builder
        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("My Application");
    }



    public void registerSensor(Sensor sensorname){
        if(sensorname==null){
            Toast.makeText(this, "Accelerometer not found", Toast.LENGTH_SHORT).show();
        }else{
            sensorManager.registerListener(this,sensorname,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }//register sensor to read data

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TAG","Foreground service running");
                        try {
                            Thread.sleep(2000);
                        }
                        catch(InterruptedException e){

                            e.printStackTrace();
                        }
                    }
                }
        ).start();

        // Update notification with initial values
        updateNotification();

        // Start as foreground service
        startForeground(NOTIFICATION_ID, notificationBuilder.build());

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Add this method to update notification content
    private void updateNotification() {
        String sensorText = "Light Sensor: " + current_light +
                "\nProximity sensor: " + current_proximity +
                "\nAccelerometer (x, y, z): " + current_accelerometer_x + ", " +
                current_accelerometer_y + ", " + current_accelerometer_z +
                "\nGyroscope (x, y, z): " + current_gyroscope_x + ", " +
                current_gyroscope_y + ", " + current_gyroscope_z;

        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(sensorText))
                .setContentText(sensorText);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        boolean valueChanged = false;

        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            current_gyroscope_x = event.values[0];
            current_gyroscope_y = event.values[1];
            current_gyroscope_z = event.values[2];
            valueChanged = true;
        }
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            current_proximity = event.values[0];
            valueChanged = true;
        }
        if(event.sensor.getType() == Sensor.TYPE_LIGHT) {
            current_light = event.values[0];
            valueChanged = true;
        }
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            current_accelerometer_x = event.values[0];
            current_accelerometer_y = event.values[1];
            current_accelerometer_z = event.values[2];
            valueChanged = true;
        }

        // Update notification if any value changed
        if (valueChanged) {
            updateNotification();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

