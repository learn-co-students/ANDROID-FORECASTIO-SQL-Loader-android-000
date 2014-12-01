package com.flatironschool.forecastdb.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.flatironschool.forecastdb.R;
import com.flatironschool.forecastdb.db.ForecastOpenHelper;

/**
 * Created by altyus on 11/11/14.
 */
public class ForecastAdapter extends CursorAdapter {
    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final TextView textView = (TextView)inflater.inflate(R.layout.simple_list_item, parent, false);
        textView.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndex(ForecastOpenHelper.COLUMN_TEMPERATURE))));

        return textView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view).setText(cursor.getString(cursor.getColumnIndex(ForecastOpenHelper.COLUMN_TEMPERATURE)));
    }
}
