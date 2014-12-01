package com.flatironschool.forecastdb.loaders;

import android.content.Context;
import android.database.Cursor;

import com.flatironschool.forecastdb.db.ForecastDataSource;

/**
 * Created by altyus on 11/24/14.
 */
public class ForecastCursorLoader extends SQLiteCursorLoader {

    private Context mContext;

    public ForecastCursorLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected Cursor loadCursor() {
        Cursor cursor = ForecastDataSource.get(mContext).selectAllTemperatures();
        return cursor;
    }
}
