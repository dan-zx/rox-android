package com.grayfox.android.data.dao.impl;

import android.content.Context;

import com.grayfox.android.data.dao.FoursquareAuthDAO;
import com.grayfox.android.data.dao.impl.sharedpreferences.FoursquareAuthSharedPreferencesDAO;

/**
 * Creates the current DAOs implementations.
 * 
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
public class DAOFactory {

    private final Context appContext;

    /**
     * Creates a DAOFactory.
     * 
     * @param context any context.
     */
    public DAOFactory(Context context) {
        appContext = context.getApplicationContext();
    }

    /** @return a new FoursquareAuthDAO. */
    public FoursquareAuthDAO getFoursquareAuthDAO() {
        return new FoursquareAuthSharedPreferencesDAO(appContext);
    }
}