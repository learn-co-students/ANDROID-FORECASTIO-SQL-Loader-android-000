package com.flatironschool.forecastdb;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.flatironschool.forecastdb.Adapters.ForecastAdapter;
import com.flatironschool.forecastdb.db.ForecastDataSource;
import com.flatironschool.forecastdb.db.ForecastOpenHelper;
import com.flatironschool.forecastdb.services.Forecast;
import com.flatironschool.forecastdb.services.ForecastService;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.SQLException;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class WeatherActivity extends ListActivity {

    private ForecastAdapter mForecastAdapter;

    protected ForecastDataSource mDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        mDataSource = new ForecastDataSource(WeatherActivity.this);
    }

    protected Callback<Forecast>mCallback = new Callback<Forecast>() {
        @Override
        public void success(Forecast forecast, Response response) {
            Log.d("TAG", forecast.toString());
            if (getListAdapter().getCount() == 0) {
                mDataSource.insertForecast(forecast);
            } else {
                mDataSource.updateTemperature(forecast);
            }
            Cursor cursor = mDataSource.selectAllTemperatures();
            updateList(cursor);
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d("TAG", "Failed");
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.refresh){
            ForecastService service = new ForecastService();
            service.loadForecastData("40.711239", " -74.010509", mCallback);
        } else if (id == R.id.delete){
            mDataSource.deleteAllTemperatures();
            updateList(mDataSource.selectAllTemperatures());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onResume(){
        super.onResume();

        try {
            mDataSource.open();
            Cursor cursor = mDataSource.selectAllTemperatures();

            mForecastAdapter = new ForecastAdapter(this, cursor, 0);
            setListAdapter(mForecastAdapter);

            //  updateList(cursor);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        try {
            mDataSource.close();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    protected void updateList(Cursor cursor){
        mForecastAdapter.changeCursor(cursor);
    }
}
