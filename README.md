ANDROID-FORECASTIO-SQL-Loader
=============================

Like always, it's time to combine a bunch of things that we've learned.  Attahed you'll find Forecast-IO with a SQL DB.  Your goal will be to add a Cursor Loader to load the forecasts in the background.  You'll also supply your location data from the device instead of hardcoding.  

##Classes 

###SQLiteCursorLoader 

* AsyncTaskLoader Subclass 
* Override loadInBackground to call abstract method "loadCursor"
* Override deliverResult to manage state of an old cached cursor vs a new cursor 
* Override onStartLoading to hand calling of deliverResult or forceLoad
* Override onStopLoading to cancel loading 
* Override onCanceled to close cursor 
* Override onReset and simply call through onStop Loading and close cursor / prevent memory leaks 

```java 

public abstract class SQLiteCursorLoader extends AsyncTaskLoader<Cursor>{
    private Cursor mCursor;

    public SQLiteCursorLoader(Context context){
        super(context);
    }

    protected abstract Cursor loadCursor();

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = loadCursor();
        if (cursor != null){
            cursor.getCount();
        }
        return cursor;
    }

    @Override
    public void deliverResult(Cursor data) {
        Cursor oldCursor = mCursor;
        mCursor = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        if (oldCursor != null && oldCursor != data && !oldCursor.isClosed()){
            oldCursor.close();
        }
    }

    @Override
    protected void onStartLoading() {
        if (mCursor != null) {
            deliverResult(mCursor);
        }
        if (takeContentChanged() || mCursor == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        onStopLoading();

        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        mCursor = null;
    }
}

```

### ForecastCursorLoader 

* extend SQLiteCurorLoader 
* Constructor: ForecastCursorLoader(Context context)
* Override abstract method `loadCursor()` This method is used to return the curor.  In our case we'll have it call selectAllTemperatures on ForecastDataSource

###Weather Activity 

WeaterActivity should be updated to have the following responsibilities: WeatherActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener

In other words, it will handle location updates and manage loading data from the CursorAdapter. 