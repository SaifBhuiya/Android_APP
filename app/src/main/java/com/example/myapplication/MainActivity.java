package com.example.myapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView gyro;
    private TextView light;
    private TextView proximity;
    private TextView acceloro;
    private SensorManager sensorManager;
    Sensor gyroSensor;
    Sensor lightSensor;
    Sensor accelSensor;
    Sensor proxiSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        gyro = findViewById(R.id.gyro_value);
        light = findViewById(R.id.light_value);
        proximity = findViewById(R.id.proxi_value);
        acceloro = findViewById(R.id.accel_value);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        proxiSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if(gyroSensor==null){
            Toast.makeText(this, "gyroSensor not found", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            sensorManager.registerListener(this,gyroSensor,SensorManager.SENSOR_DELAY_NORMAL);
        }

        if(proxiSensor==null){
            Toast.makeText(this, "proxiSensor not found", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            sensorManager.registerListener(this,gyroSensor,SensorManager.SENSOR_DELAY_NORMAL);
        }

        if(lightSensor==null){
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            sensorManager.registerListener(this,lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
        }

        if(accelSensor==null){
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            sensorManager.registerListener(this,accelSensor,SensorManager.SENSOR_DELAY_NORMAL);
        }


    } //On create

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE){
            gyro.setText("Rotation " + event.values[0]);
        }
        if(event.sensor.getType()==Sensor.TYPE_PROXIMITY){
            proximity.setText("Distance " + event.values[0]);
        }
        if(event.sensor.getType()==Sensor.TYPE_LIGHT){
            light.setText("Distance " + event.values[0]);
        }
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            acceloro.setText("accelerometer " + event.values[0]);
        }

    }//On sensor changes

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }//On accuracy changed


    @Override
    protected void onStop(){
        super.onStop();
        sensorManager.unregisterListener(this);
    }

}