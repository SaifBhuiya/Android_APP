package com.example.myapplication;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class Foreground extends Service {
    private float current_light = 0;
    private float current_proximity = 0;
    private float current_accelerometer_x = 0;
    private float current_accelerometer_y = 0;
    private float current_accelerometer_z = 0;
    private float current_gyroscope_x = 0;
    private float current_gyroscope_y = 0;
    private float current_gyroscope_z = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

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

                        }
                    }
                }
        ).start();
        final String CHANNEL_ID="Foreground Service";



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID,
                    NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("My Application")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Light Sensor: " + current_light + "\t Proximity sensor: "+ current_proximity +
                        "\tAccelerometer (x, y, z) : " + current_accelerometer_x +", "+ current_accelerometer_y + ", " +current_accelerometer_z+"\n" +
                        "\nGyroscope (x, y, z) :"  + current_gyroscope_x + ", " + current_gyroscope_y + ", " + current_gyroscope_z ))
                .setContentText("Light Sensor: " + current_light + "\t Proximity sensor: "+ current_proximity +
                        "\tAccelerometer (x, y, z) : " + current_accelerometer_x +", "+ current_accelerometer_y + ", " +current_accelerometer_z+"\n" +
                        "\nGyroscope (x, y, z) :"  + current_gyroscope_x + ", " + current_gyroscope_y + ", " + current_gyroscope_z  );
        startForeground(1001,notification.build());
        return super.onStartCommand(intent, flags, startId);
    }


}

