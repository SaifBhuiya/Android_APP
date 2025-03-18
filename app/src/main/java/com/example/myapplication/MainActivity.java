package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private TextView testData;

    private TextView gyro;
    private TextView light;
    private TextView proximity;
    private TextView acceloro;
    private SensorManager sensorManager;
    Sensor gyroSensor;
    Sensor lightSensor;
    Sensor accelSensor;
    Sensor proxiSensor;
    private DbHelper dbHelper;

    private float current_light;
    private float current_proximity;
    private String current_accelerometer;
    private String current_gyroscope;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        initialize();


        registerSensor(gyroSensor);
        registerSensor(proxiSensor);
        registerSensor(lightSensor);
        registerSensor(accelSensor);

    } //On create
    public void initialize(){
        dbHelper = new DbHelper(this);


        gyro = findViewById(R.id.gyro_value);
        light = findViewById(R.id.light_value);
        proximity = findViewById(R.id.proxi_value);
        acceloro = findViewById(R.id.accel_value);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        proxiSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        CardView light_card = findViewById(R.id.light_sensor);
        CardView proxi_card = findViewById(R.id.proximity_sensor);
        CardView accel_card = findViewById(R.id.accelerometer);
        CardView gyro_card = findViewById(R.id.gyroscope);
        setClickListeners(light_card);
        setClickListeners(proxi_card);
        setClickListeners(accel_card);
        setClickListeners(gyro_card);

    }

    public void setClickListeners(CardView cardname){

        cardname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSensorData(current_light ,current_proximity,current_accelerometer,current_gyroscope);
                Toast.makeText(MainActivity.this, "Light Card clicked!", Toast.LENGTH_SHORT).show();
                setContentView(R.layout.view_timeseries_graph);
                testData = findViewById(R.id.light_data);
                testData.setText(readSensorData());
            }
        });

    }
    public void back_button_click(View v){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
//        setContentView(R.layout.activity_main);


        }
    public void registerSensor(Sensor sensorname){
        if(sensorname==null){
            Toast.makeText(this, "Accelerometer not found", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            sensorManager.registerListener(this,sensorname,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public String readSensorData(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String data = "";
        Cursor cursor = db.query("sensors", new String[]{"light", "proximity", "accelerometer", "gyroscope"},
                null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Extract the values from the cursor with safe column index checks
                int lightIndex = cursor.getColumnIndex("light");
                int proximityIndex = cursor.getColumnIndex("proximity");
                int accelerometerIndex = cursor.getColumnIndex("accelerometer");
                int gyroscopeIndex = cursor.getColumnIndex("gyroscope");

                // Check if the column exists before accessing the value
                if (lightIndex != -1) {
                    float light = cursor.getFloat(lightIndex);
                    data += "\nLight: " + light ;
                }
                if (proximityIndex != -1) {
                    float proximity = cursor.getFloat(proximityIndex);
                    data += "\nProximity: " + proximity ;
                }
                if (accelerometerIndex != -1) {
                    String accelerometer = cursor.getString(accelerometerIndex);
                    data += "\n" + accelerometer;
                }
                if (gyroscopeIndex != -1) {
                    String gyroscope = cursor.getString(gyroscopeIndex);
                    data += "\n" + gyroscope +"\n";
                }
            }
            cursor.close();
        }
        db.close();
        return data;
    }


    public void insertSensorData(float light, float proximity, String accelerometer, String gyroscope) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("light", light);
        values.put("proximity", proximity);
        values.put("accelerometer", accelerometer);
        values.put("gyroscope", gyroscope);

        db.insert("sensors", null, values);
        db.close();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE){
            gyro.setText("Gyroscope\n" + "X: " + event.values[0] + "\n" + "Y: " +event.values[1] + "\n" + "Z: " +event.values[2]);
            current_gyroscope = ("Gyroscope\n" + "X: " + event.values[0] + "\n" + "Y: " +event.values[1] + "\n" + "Z: " +event.values[2]);
        }
        if(event.sensor.getType()==Sensor.TYPE_PROXIMITY){
            proximity.setText("Proximity Sensor\n" + event.values[0]);
            current_proximity = event.values[0];
        }
        if(event.sensor.getType()==Sensor.TYPE_LIGHT){
            light.setText("Light Sensor\n" + event.values[0]);
            current_light = event.values[0];
        }
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            acceloro.setText("Accelerometer\n" + "X: " + event.values[0] + "\n" + "Y: " +event.values[1] + "\n" + "Z: " +event.values[2] );
            current_accelerometer = ("Accelerometer\n" + "X: " + event.values[0] + "\n" + "Y: " +event.values[1] + "\n" + "Z: " +event.values[2] );
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