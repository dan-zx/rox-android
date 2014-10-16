package com.grayfox.android.data.dao.impl.sharedpreferences;

import android.content.Context;
import android.preference.PreferenceManager;

import com.grayfox.android.R;
import com.grayfox.android.data.dao.FoursquareAuthDAO;

/**
 * Foursquare Authentication DAO whose data source are shared preferences.
 * 
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
public class FoursquareAuthSharedPreferencesDAO implements FoursquareAuthDAO {

    private final Context context;

    /**
     * Creates a new FoursquareAuthSharedPreferencesDAO.
     * 
     * @param context the application context.
     */
    public FoursquareAuthSharedPreferencesDAO(Context context) {
        this.context = context;
    }

    /**
     * Fetches the current authentication code from shared preferences.
     * 
     * @return an authentication code.
     */
    @Override
    public String fetchAuthCode() {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.foursquare_auth_code_key), null);
    }

    /**
     * Saves to shared preferences the current authentication code.
     * 
     * @param authCode an authentication code.
     */
    @Override
    public void saveAuthCode(String authCode) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(context.getString(R.string.foursquare_auth_code_key), authCode)
            .commit();
    }

    /** Deletes the current authentication code from shared preferences. */
    @Override
    public void deleteAuthCode() {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .remove(context.getString(R.string.foursquare_auth_code_key))
            .commit();
    }
}