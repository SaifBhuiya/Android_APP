package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private TextView testData, gyro, light, proximity, acceloro;
    private SensorManager sensorManager;
    private Sensor gyroSensor, lightSensor, accelSensor, proxiSensor;
    private DbHelper dbHelper;
    private float current_light, current_proximity,current_accelerometer_x,current_accelerometer_y,current_accelerometer_z,current_gyroscope_x,current_gyroscope_y,current_gyroscope_z;

    private Timer timer;
    private TimerTask timerTask;

    private LineChart lineChart;
    private List<String> xValues;

    String dataList,xdata,ydata,zdata = "";







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        timer = new Timer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                // Your background task here, for example:
                insertSensorData(current_light,current_proximity,current_accelerometer_x,current_accelerometer_y,current_accelerometer_z,current_gyroscope_x,current_gyroscope_y,current_gyroscope_z);

            }
        };

//        timer.scheduleAtFixedRate(timerTask, 0, 300000);
        timer.scheduleAtFixedRate(timerTask, 0, 3000);

        //setting values to the variables declared earlier
        initialize();

        //calling function to registerListeners for sensor
        registerSensor(gyroSensor);
        registerSensor(proxiSensor);
        registerSensor(lightSensor);
        registerSensor(accelSensor);


    }
    public void initialize(){
        dbHelper = new DbHelper(this);

        // setting TextView variables
        gyro = findViewById(R.id.gyro_value);
        light = findViewById(R.id.light_value);
        proximity = findViewById(R.id.proxi_value);
        acceloro = findViewById(R.id.accel_value);

        //setting sensor manager to access sensors
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //getting individual sensors into variables
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        proxiSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //getting cards into variables
        CardView light_card = findViewById(R.id.light_sensor);
        CardView proxi_card = findViewById(R.id.proximity_sensor);
        CardView accel_card = findViewById(R.id.accelerometer);
        CardView gyro_card = findViewById(R.id.gyroscope);

        //setting onClick Listeners to Cards
        setClickListeners(light_card,0);
        setClickListeners(proxi_card,1);
        setClickListeners(accel_card,2);
        setClickListeners(gyro_card,3);

    }


    public void setClickListeners(CardView cardname,int sensornum){
        //cardname = the card variable
        cardname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.view_timeseries_graph);
               // Toast.makeText(MainActivity.this,"", Toast.LENGTH_SHORT).show();

                //testing if data loads successfully
                testData = findViewById(R.id.light_data);
                if(sensornum==0){
                    testData.setText(readSensorData("light"));
                    LightChart();
                }
                else if(sensornum==1){
                    testData.setText(readSensorData("proximity"));
                    ProximityChart();
                } else if (sensornum==2) {
                    testData.setText(readSensorData("accelerometer"));
                    AccelerometerChart();

                } else{
                    testData.setText(readSensorData("gyroscope"));
                   GyroscopeChart();
                }


    }
        });
    }
    private void LightChart(){
        //https://www.youtube.com/watch?v=KIW4Vp8mjLo

        lineChart = findViewById(R.id.lineChart);
        Description description = new Description();
        description.setText("Sensor Value");
        description.setPosition(150f,15f);
        lineChart.setDescription(description);
        lineChart.getAxisRight().setDrawLabels(false);

//        xValues = Arrays.asList("Nadun", "Kural", "Panther");
//
//        XAxis xAxis = lineChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
//        xAxis.setLabelCount(3);
//        xAxis.setGranularity(1f);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        //limiting max scale to view clear graph
        yAxis.setAxisMaximum(500f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);

        List<Entry> entries = new ArrayList<>();
        String[] items = dataList.split(",");
        int index = 0;
        for (String item : items) {
            entries.add(new Entry(index,Float.parseFloat(item)));

//        //update x axis value
//        List<String> xValues = new ArrayList<>(); // Create a dynamic list
//        xValues.add(index + " mins");
//        XAxis xAxis = lineChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
//        xAxis.setLabelCount(xValues.size(), true);
//        xAxis.setGranularity(1f);
//        lineChart.invalidate(); // Refresh chart


            //increment index for 5 mins
        index+=5;
        }


        LineDataSet dataSet1 = new LineDataSet(entries, "Light Sensor data");
        dataSet1.setColor(Color.BLUE);

        LineData lineData = new LineData(dataSet1);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private void ProximityChart(){

        lineChart = findViewById(R.id.lineChart);
        Description description = new Description();
        description.setText("Sensor Value");
        description.setPosition(150f,15f);
        lineChart.setDescription(description);
        lineChart.getAxisRight().setDrawLabels(false);



        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(5f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);

        List<Entry> entries = new ArrayList<>();
        String[] items = dataList.split(",");
        int index = 0;
        for (String item : items) {
            entries.add(new Entry(index,Float.parseFloat(item)));


            //increment index for 5 mins
            index+=5;
        }


        LineDataSet dataSet1 = new LineDataSet(entries, "Accelerometer Sensor data");
        dataSet1.setColor(Color.BLUE);

        LineData lineData = new LineData(dataSet1);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private void GyroscopeChart(){
        //https://www.youtube.com/watch?v=KIW4Vp8mjLo

        lineChart = findViewById(R.id.lineChart);
        Description description = new Description();
        description.setText("Sensor Value");
        description.setPosition(150f,15f);
        lineChart.setDescription(description);
        lineChart.getAxisRight().setDrawLabels(false);


        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(-10f);
        //limiting max scale to view clear graph
        yAxis.setAxisMaximum(10f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);

        List<Entry> entries = new ArrayList<>();
        String[] items = xdata.split(",");
        int index = 0;
        for (String item : items) {
            entries.add(new Entry(index,Float.parseFloat(item)));
            index++;
        }
        List<Entry> entries2 = new ArrayList<>();
        String[] items2 = ydata.split(",");
        index = 0;
        for (String item : items2) {
            entries2.add(new Entry(index,Float.parseFloat(item)));
            index++;
        }
        List<Entry> entries3 = new ArrayList<>();
        String[] items3 = zdata.split(",");
        index = 0;
        for (String item : items3) {

            entries3.add(new Entry(index,Float.parseFloat(item)));
            index++;

        }


        LineDataSet dataSet1 = new LineDataSet(entries, "Gyroscope X-axis data");
        dataSet1.setColor(Color.BLUE);

        LineDataSet dataSet2 = new LineDataSet(entries2, "Gyroscope Y-axis data");
        dataSet2.setColor(Color.RED);

        LineDataSet dataSet3 = new LineDataSet(entries3, "Gyroscope Z-axis data");
        dataSet3.setColor(Color.GREEN);

        LineData lineData = new LineData(dataSet1,dataSet2,dataSet3);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
    private void AccelerometerChart(){
        //https://www.youtube.com/watch?v=KIW4Vp8mjLo

        lineChart = findViewById(R.id.lineChart);
        Description description = new Description();
        description.setText("Sensor Value");
        description.setPosition(150f,15f);
        lineChart.setDescription(description);
        lineChart.getAxisRight().setDrawLabels(false);


        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(-10f);
        //limiting max scale to view clear graph
        yAxis.setAxisMaximum(10f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);

        List<Entry> entries = new ArrayList<>();
        String[] items = xdata.split(",");
        int index = 0;
        for (String item : items) {
            entries.add(new Entry(index,Float.parseFloat(item)));
            index++;
        }
        List<Entry> entries2 = new ArrayList<>();
        String[] items2 = ydata.split(",");
        index = 0;
        for (String item : items2) {
            entries2.add(new Entry(index,Float.parseFloat(item)));
            index++;
        }
        List<Entry> entries3 = new ArrayList<>();
        String[] items3 = zdata.split(",");
        index = 0;
        for (String item : items3) {

            entries3.add(new Entry(index,Float.parseFloat(item)));
            index++;

        }


        LineDataSet dataSet1 = new LineDataSet(entries, "Accelerometer X-axis data");
        dataSet1.setColor(Color.BLUE);

        LineDataSet dataSet2 = new LineDataSet(entries2, "Accelerometer Y-axis data");
        dataSet2.setColor(Color.RED);

        LineDataSet dataSet3 = new LineDataSet(entries3, "Accelerometer Z-axis data");
        dataSet3.setColor(Color.GREEN);

        LineData lineData = new LineData(dataSet1,dataSet2,dataSet3);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    public void registerSensor(Sensor sensorname){
        if(sensorname==null){
            Toast.makeText(this, "Accelerometer not found", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            sensorManager.registerListener(this,sensorname,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public String extractValue(String input, String label, String endDelimiter) {
        int startIndex = input.indexOf(label);

        if (startIndex == -1) {
            return null; // Label not found
        }

        startIndex += label.length(); // Move index past the label
        int endIndex = input.indexOf(endDelimiter, startIndex); // Find the custom end delimiter

        if (endIndex == -1) {
            // If the end delimiter is not found, take the rest of the string
            endIndex = input.length();
        }

        return input.substring(startIndex, endIndex).trim(); // Extract and return the value
    }

    //returns view to home page and reinitialized view
    public void back_button_click(View v){

        setContentView(R.layout.activity_main);
        initialize();


        }


    public String readSensorData(String columnName){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        dataList = "";
        xdata = "";
        ydata = "";
        zdata = "";

        Cursor cursor;

        // If the column is gyroscope, query all three sub-columns
        if (columnName.equals("gyroscope")) {
            cursor = db.query("sensors", new String[]{"gyroscope_x", "gyroscope_y", "gyroscope_z"},
                    null, null, null, null, null);
        }
        else if(columnName.equals("accelerometer")) {
            cursor = db.query("sensors", new String[]{"accelerometer_x", "accelerometer_y", "accelerometer_z"},
                    null, null, null, null, null);
        }
        else {
            cursor = db.query("sensors", new String[]{columnName},
                    null, null, null, null, null);
        }

        if (cursor != null) {
            while (cursor.moveToNext()) {
                if (columnName.equals("light") || columnName.equals("proximity")) {
                    int columnIndex = cursor.getColumnIndex(columnName);
                    if (columnIndex != -1) {
                        float value = cursor.getFloat(columnIndex);
                        dataList += value + ",";
                    }
                }
                if (columnName.equals("gyroscope")) {
                    int xIndex = cursor.getColumnIndex("gyroscope_x");
                    int yIndex = cursor.getColumnIndex("gyroscope_y");
                    int zIndex = cursor.getColumnIndex("gyroscope_z");

                    if (xIndex != -1) xdata += cursor.getFloat(xIndex) + ",";
                    if (yIndex != -1) ydata += cursor.getFloat(yIndex) + ",";
                    if (zIndex != -1) zdata += cursor.getFloat(zIndex) + ",";

                    // Display xdata to test
//                   Toast.makeText(MainActivity.this, "X: " + zdata, Toast.LENGTH_SHORT).show();
                }
                if(columnName.equals("accelerometer")) {
                    int xIndex = cursor.getColumnIndex("accelerometer_x");
                    int yIndex = cursor.getColumnIndex("accelerometer_y");
                    int zIndex = cursor.getColumnIndex("accelerometer_z");

                    if (xIndex != -1) xdata += cursor.getFloat(xIndex) + ",";
                    if (yIndex != -1) ydata += cursor.getFloat(yIndex) + ",";
                    if (zIndex != -1) zdata += cursor.getFloat(zIndex) + ",";

                    // Display xdata to test
                   //Toast.makeText(MainActivity.this, "X: " + xdata, Toast.LENGTH_SHORT).show();
                }
            }
            cursor.close();
        }
        db.close();

        return dataList; // Change this if you want to return gyroscope data instead
    }



    public void insertSensorData(float light, float proximity, float accelerometer_x, float accelerometer_y,float accelerometer_z,float gyroscope_x, float gyroscope_y, float gyroscope_z) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("light", light);
        values.put("proximity", proximity);
        values.put("accelerometer_x", accelerometer_x);
        values.put("accelerometer_y", accelerometer_y);
        values.put("accelerometer_z", accelerometer_z);
        values.put("gyroscope_x", gyroscope_x);
        values.put("gyroscope_y", gyroscope_y);
        values.put("gyroscope_z", gyroscope_z);



        db.insert("sensors", null, values);
        db.close();

    }// write data to database

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE){
            gyro.setText("Gyroscope\n" + "X: " + event.values[0] + "\n" + "Y: " +event.values[1] + "\n" + "Z: " +event.values[2]);
            current_gyroscope_x = (event.values[0]);
            current_gyroscope_y = (event.values[1]);
            current_gyroscope_z = (event.values[2]);

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
            current_accelerometer_x = event.values[0];
            current_accelerometer_y = event.values[1];
            current_accelerometer_z = event.values[2];
        }

    }//handle home screen values

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    protected void onStop(){
        super.onStop();
        sensorManager.unregisterListener(this);
    }

}