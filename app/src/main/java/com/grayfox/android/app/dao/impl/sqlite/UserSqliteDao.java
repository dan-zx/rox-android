package com.grayfox.android.app.dao.impl.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.google.inject.name.Named;

import com.grayfox.android.app.dao.UserDao;
import com.grayfox.android.client.model.User;

import javax.inject.Inject;

public class UserSqliteDao implements UserDao {

    private static final int USER_ID = 1;
    private static final String TAG = UserSqliteDao.class.getSimpleName();
    private static final String WHERE_ID_CLAUSE = UserEntry._ID + " = ?";

    private final SQLiteOpenHelper databaseHelper;

    @Inject
    public UserSqliteDao(@Named("GrayFoxDbHelper") SQLiteOpenHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @Override
    public User fetchCurrent() {
        Cursor cursor = null;
        User user = null;
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        try {
            String[] columns = {UserEntry._ID, UserEntry.NAME, UserEntry.LAST_NAME, UserEntry.PHOTO_URL, UserEntry.FOURSQUARE_ID};
            cursor = database.query(UserEntry.TABLE_NAME, columns, WHERE_ID_CLAUSE, new String[]{String.valueOf(USER_ID)}, null, null, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                user = new User();
                user.setName(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.NAME)));
                user.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.LAST_NAME)));
                user.setPhotoUrl(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.PHOTO_URL)));
                user.setFoursquareId(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.FOURSQUARE_ID)));
            }
        } catch (Exception ex) {
            Log.e(TAG, "Could not complete fetch current user", ex);
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception ex) {
                    Log.e(TAG, "Couldn't close cursor correctly");
                }
            }
            database.close();
        }
        return user;
    }

    @Override
    public void saveOrUpdate(User user) {
        if (fetchCurrent() != null) update(user);
        else save(user);
    }

    @Override
    public void deleteCurrent() {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        database.beginTransaction();
        try {
            String[] whereArgs = {String.valueOf(USER_ID)};
            database.delete(UserEntry.TABLE_NAME, WHERE_ID_CLAUSE, whereArgs);
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e(TAG, "Could not complete delete current user", ex);
        } finally {
            database.endTransaction();
            database.close();
        }
    }

    private void save(User user) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(UserEntry._ID, USER_ID);
            values.put(UserEntry.NAME, user.getName());
            values.put(UserEntry.LAST_NAME, user.getLastName());
            values.put(UserEntry.PHOTO_URL, user.getPhotoUrl());
            values.put(UserEntry.FOURSQUARE_ID, user.getFoursquareId());
            database.insert(UserEntry.TABLE_NAME, null, values);
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e(TAG, "Could not complete insert [" + user + "]", ex);
        } finally {
            database.endTransaction();
            database.close();
        }
    }

    private void update(User user) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(UserEntry.NAME, user.getName());
            values.put(UserEntry.LAST_NAME, user.getLastName());
            values.put(UserEntry.PHOTO_URL, user.getPhotoUrl());
            values.put(UserEntry.FOURSQUARE_ID, user.getFoursquareId());
            String[] whereArgs = {String.valueOf(USER_ID)};
            database.update(UserEntry.TABLE_NAME, values, WHERE_ID_CLAUSE, whereArgs);
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e(TAG, "Could not complete update [" + user + "]", ex);
        } finally {
            database.endTransaction();
            database.close();
        }
    }

    private static class UserEntry implements BaseColumns {

        private static final String TABLE_NAME = "user";
        private static final String NAME = "name";
        private static final String LAST_NAME = "last_name";
        private static final String PHOTO_URL = "photo_url";
        private static final String FOURSQUARE_ID = "foursquare_id";
    }
}