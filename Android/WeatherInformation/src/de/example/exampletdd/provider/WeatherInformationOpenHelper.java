package de.example.exampletdd.provider;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherInformationOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "WeatherInformationOpenHelper";

    private static final String DATABASE_NAME = "weatherinformation.db";
    private static final int DATABASE_VERSION = 1;

    WeatherInformationOpenHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public WeatherInformationOpenHelper(final Context context, final String name,
            final CursorFactory factory, final int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    public WeatherInformationOpenHelper(final Context context, final String name,
            final CursorFactory factory, final int version,
            final DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        // TODO Auto-generated method stub

    }

}
