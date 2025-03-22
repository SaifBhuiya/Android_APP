package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ServiceHandler extends Worker {
    private SensorManager sensorManager;
    private Sensor gyroSensor, lightSensor, accelSensor, proxiSensor;
    private DbHelper dbHelper;
    private float current_light, current_proximity, current_accelerometer_x, current_accelerometer_y, current_accelerometer_z, current_gyroscope_x, current_gyroscope_y, current_gyroscope_z;
   // private static final String CHANNEL_ID = "workmanager_channel";

    public ServiceHandler(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        dbHelper = new DbHelper(context);

        // Initialize Sensor Manager
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            proxiSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

//    private void showNotification(String title, String message) {
//        Context context = getApplicationContext();
//
//        Intent intent = new Intent(context, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
//        );
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        if (notificationManager != null) {
//            notificationManager.notify(1, builder.build());
//        }
//    }
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Android 8+ requires notification channels
//            CharSequence name = "WorkManager Notifications";
//            String description = "Notifications from WorkManager background tasks";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//
//            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
//            if (notificationManager != null) {
//                notificationManager.createNotificationChannel(channel);
//            }
//        }
//    }

    @NonNull
    @Override
    public Result doWork() {
        if (sensorManager == null) {
            return Result.failure();
        }
      //  createNotificationChannel();

        // Register sensors and collect data
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

//        showNotification("Sensor Application", "Light Sensor: " + current_light + "\t Proximity sensor: "+ current_proximity
//        + "\tAccelerometer (x, y, z) : "+ current_accelerometer_x + ", " +current_accelerometer_y + ", " +current_accelerometer_z+
//                "\nGyroscope (x, y, z) : " + current_gyroscope_x + ", " + current_gyroscope_y + ", "+current_gyroscope_z );

        return Result.success();
    }
}
