package com.flatironschool.forecastdb.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.flatironschool.forecastdb.services.Forecast;

import java.sql.SQLException;

/**
 * Created by altyus on 8/6/14.
 */
public class ForecastDataSource {

    private SQLiteDatabase mDatabase;
    private ForecastOpenHelper mForecastOpenHelper;
    private Context mContext;

    public ForecastDataSource(Context context){
        mContext = context;
        mForecastOpenHelper = new ForecastOpenHelper(mContext);
    }

    //open Database
    public void open() throws SQLException{
        mDatabase = mForecastOpenHelper.getWritableDatabase();
    }
    //close Database
    public void close() {
        mDatabase.close();
    }
    //insert
    public void insertForecast(Forecast forecast) {

        mDatabase.beginTransaction();

        try {
            for (Forecast.HourData hour : forecast.hourly.data) {
                ContentValues values = new ContentValues();
                values.put(ForecastOpenHelper.COLUMN_TEMPERATURE, hour.temperature);
                values.put(ForecastOpenHelper.COLUMN_HUMIDITY, hour.humidity);
                values.put(ForecastOpenHelper.COLUMN_ICON, hour.icon);
                values.put(ForecastOpenHelper.COLUMN_OZONE, hour.ozone);
                values.put(ForecastOpenHelper.COLUMN_PRECIPPROBABILITY, hour.precipProbability);
                values.put(ForecastOpenHelper.COLUMN_PRESSURE, hour.pressure);
                values.put(ForecastOpenHelper.COLUMN_SUMMARY, hour.summary);
                values.put(ForecastOpenHelper.COLUMN_VISIBILITY, hour.visibility);
                values.put(ForecastOpenHelper.COLUMN_WINDSPEED, hour.windSpeed);
                values.put(ForecastOpenHelper.COLUMN_TIME, hour.time);

                mDatabase.insert(ForecastOpenHelper.TABLE_FORECASTS, null, values);
            }
            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }
    }

    //select
    public Cursor selectAllTemperatures() {
        Cursor cursor = mDatabase.query(
                ForecastOpenHelper.TABLE_FORECASTS, //table
                new String[]{
                        ForecastOpenHelper.COLUMN_ID,
                        ForecastOpenHelper.COLUMN_TEMPERATURE, ForecastOpenHelper.COLUMN_HUMIDITY,
                        ForecastOpenHelper.COLUMN_ICON, ForecastOpenHelper.COLUMN_OZONE,
                        ForecastOpenHelper.COLUMN_PRECIPPROBABILITY, ForecastOpenHelper.COLUMN_PRESSURE,
                        ForecastOpenHelper.COLUMN_SUMMARY, ForecastOpenHelper.COLUMN_VISIBILITY,
                        ForecastOpenHelper.COLUMN_WINDSPEED, ForecastOpenHelper.COLUMN_TIME
                },
                null, //where clause
                null, //where params
                null, //groupby
                null, //having
                null //orderby

        );
        return cursor;
    }

    public Cursor selectForecast(double time){
        String whereClause = ForecastOpenHelper.COLUMN_TIME + " == ?";

        Cursor cursor = mDatabase.query(
                ForecastOpenHelper.TABLE_FORECASTS, //table
                new String[]{
                        ForecastOpenHelper.COLUMN_TEMPERATURE, ForecastOpenHelper.COLUMN_HUMIDITY,
                        ForecastOpenHelper.COLUMN_ICON, ForecastOpenHelper.COLUMN_OZONE,
                        ForecastOpenHelper.COLUMN_PRECIPPROBABILITY, ForecastOpenHelper.COLUMN_PRESSURE,
                        ForecastOpenHelper.COLUMN_SUMMARY, ForecastOpenHelper.COLUMN_VISIBILITY,
                        ForecastOpenHelper.COLUMN_WINDSPEED, ForecastOpenHelper.COLUMN_TIME
                },
                whereClause,
                new String[]{String.valueOf(time)},
                null,
                null,
                null
        );
        return cursor;
    }

    //update

    public int updateTemperature(Forecast forecast){
        String whereClause = ForecastOpenHelper.COLUMN_TIME + " == ?";

        int rowsupdated = 0;

        for (Forecast.HourData hour : forecast.hourly.data) {
            ContentValues values = new ContentValues();
            values.put(ForecastOpenHelper.COLUMN_TEMPERATURE, hour.temperature);
            values.put(ForecastOpenHelper.COLUMN_HUMIDITY, hour.humidity);
            values.put(ForecastOpenHelper.COLUMN_ICON, hour.icon);
            values.put(ForecastOpenHelper.COLUMN_OZONE, hour.ozone);
            values.put(ForecastOpenHelper.COLUMN_PRECIPPROBABILITY, hour.precipProbability);
            values.put(ForecastOpenHelper.COLUMN_PRESSURE, hour.pressure);
            values.put(ForecastOpenHelper.COLUMN_SUMMARY, hour.summary);
            values.put(ForecastOpenHelper.COLUMN_VISIBILITY, hour.visibility);
            values.put(ForecastOpenHelper.COLUMN_WINDSPEED, hour.windSpeed);
            values.put(ForecastOpenHelper.COLUMN_TIME, hour.time);
            rowsupdated += mDatabase.update(
                    ForecastOpenHelper.TABLE_FORECASTS,
                    values,
                    whereClause, //where clause
                    new String []{String.valueOf(hour.time)} // where args
            );
        }
        return rowsupdated;
    }

    //delete

    public int deleteAllTemperatures() {
        return mDatabase.delete(ForecastOpenHelper.TABLE_FORECASTS,null, null);
    }

    public int delete(int forecastID){
        String whereClause = ForecastOpenHelper.COLUMN_ID + " == ?";
        return mDatabase.delete(ForecastOpenHelper.TABLE_FORECASTS, whereClause, new String[]{String.valueOf(forecastID)});
    }
}
