package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(Context context) {
        super(context, "sensor.db", null, 1);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE sensors (id INTEGER PRIMARY KEY AUTOINCREMENT, light REAL, proximity REAL, accelerometer_x REAL, accelerometer_y REAL, accelerometer_z REAL, gyroscope_x REAL,gyroscope_y REAL, gyroscope_z REAL)"));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
