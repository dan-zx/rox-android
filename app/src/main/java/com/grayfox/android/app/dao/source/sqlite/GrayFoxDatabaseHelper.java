package com.grayfox.android.app.dao.source.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.grayfox.android.app.R;

public class GrayFoxDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = GrayFoxDatabaseHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "gray-fox.db";
    private static final int DATABASE_VERSION = 1;

    private final Context context;

    public GrayFoxDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(TAG, "Creating database version " + DATABASE_VERSION + "...");
        String[] statements = context.getResources().getStringArray(R.array.create_db_schema);
        for (String statement : statements) {
            Log.v(TAG, statement);
            db.execSQL(statement);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        context.deleteDatabase(DATABASE_NAME);
        onCreate(db);
    }
}