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
        db.execSQL(String.format("CREATE TABLE sensors (id INTEGER PRIMARY KEY AUTOINCREMENT, light REAL, proximity REAL,accelerometer TEXT, gyroscope TEXT)"));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
