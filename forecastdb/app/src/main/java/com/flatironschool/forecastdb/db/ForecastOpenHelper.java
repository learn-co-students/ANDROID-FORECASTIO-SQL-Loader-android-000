package com.flatironschool.forecastdb.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by altyus on 8/6/14.
 */

public class ForecastOpenHelper extends SQLiteOpenHelper {

    public static final String TABLE_FORECASTS = "FORECASTS";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TEMPERATURE = "TEMPERATURE";
    public static final String COLUMN_PRECIPPROBABILITY = "PRECIPPROBABILITY";
    public static final String COLUMN_HUMIDITY = "HUMIDITY";
    public static final String COLUMN_WINDSPEED = "WINDSPEED";
    public static final String COLUMN_VISIBILITY = "VISIBILITY";
    public static final String COLUMN_PRESSURE = "PRESSURE";
    public static final String COLUMN_OZONE = "OZONE";
    public static final String COLUMN_ICON = "ICON";
    public static final String COLUMN_SUMMARY = "SUMMARY";
    public static final String COLUMN_TIME = "TIME";

    private static final String DB_NAME = "forecasts.db";
    private static final int DB_VERSION = 1;

    private static final String DB_CREATE =
            "CREATE TABLE " + TABLE_FORECASTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    COLUMN_TEMPERATURE + " REAL, "+
                    COLUMN_PRECIPPROBABILITY + " REAL, "+
                    COLUMN_HUMIDITY + " REAL, "+
                    COLUMN_WINDSPEED + " REAL, "+
                    COLUMN_VISIBILITY + " REAL, "+
                    COLUMN_PRESSURE + " REAL, "+
                    COLUMN_OZONE + " REAL, "+
                    COLUMN_ICON + " VARCHAR(255), "+
                    COLUMN_SUMMARY + " VARCHAR(255), " +
                    COLUMN_TIME + " REAL)";

    public ForecastOpenHelper(Context context){
        super(context,DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
