package com.flatironschool.forecastdb;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.IntentSender;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.flatironschool.forecastdb.Adapters.ForecastAdapter;
import com.flatironschool.forecastdb.db.ForecastDataSource;
import com.flatironschool.forecastdb.loaders.ForecastCursorLoader;
import com.flatironschool.forecastdb.services.Forecast;
import com.flatironschool.forecastdb.services.ForecastService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import java.sql.SQLException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class WeatherActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

    protected ForecastDataSource mDataSource;
    private final static String TAG = "WeatherActivity";

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationClient mLocationClient;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private static final long UPDATE_INTERVAL = 30000;
    private static final long FASTEST_INTERVAL = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        mDataSource = ForecastDataSource.get(this);

        if (servicesConnected()) {
            mLocationClient = new LocationClient(this, this, this);

            mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (servicesConnected()) {
            mLocationClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if (servicesConnected() && mLocationClient != null) {
            mLocationClient.disconnect();
        }
        super.onStop();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new ForecastCursorLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ForecastAdapter adapter = new ForecastAdapter(this, data, 0);

        setListAdapter(adapter);

        Toast.makeText(this, "Loaded " + data.getCount() + " records", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        setListAdapter(null);
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

            getLoaderManager().restartLoader(0, null, WeatherActivity.this);
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
            if (mCurrentLocation != null) {
                service.loadForecastData(String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude()), mCallback);
            }
                else {
                Toast.makeText(this, "There was an error", Toast.LENGTH_SHORT);
            }
        } else if (id == R.id.delete){
            int recordsDeleted  = mDataSource.deleteAllTemperatures();
            getLoaderManager().restartLoader(0, null, this);

            Toast.makeText(this, "Deleted " + recordsDeleted + " records", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
                switch (resultCode) {
                    case Activity.RESULT_OK :

                        break;
                }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        try {
            mDataSource.open();
            getLoaderManager().initLoader(0, null, this);
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

    private boolean servicesConnected() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS == resultCode) {
            Log.d(TAG, "Google Play services are available");

            return true;
        } else {

            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);
            if (errorDialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getFragmentManager(), "Location Updates");
            }
        }

        return false;
    }
    public static class ErrorDialogFragment extends DialogFragment {
        private Dialog mDialog;
        public ErrorDialogFragment(){
            super();
            mDialog = null;
        }
        public void setDialog(Dialog dialog){
            mDialog = dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    //Connection Callbacks
    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        if (mLocationClient != null && mLocationRequest != null) {
            mLocationClient.requestLocationUpdates(mLocationRequest, this);
        }
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    //Connection Failed callback
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()){
            try {
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            //show an error dialog
        }
    }

    //Location Listener Callback
    @Override
    public void onLocationChanged(Location location) {
        if (mCurrentLocation == null) {
            ForecastService service = new ForecastService();
            service.loadForecastData(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), mCallback);
        }

        mCurrentLocation = location;
    }

}
