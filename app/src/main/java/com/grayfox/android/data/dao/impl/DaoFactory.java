package com.grayfox.android.data.dao.impl;

import android.content.Context;

import com.grayfox.android.data.dao.FoursquareAuthDao;
import com.grayfox.android.data.dao.impl.sharedpreferences.FoursquareAuthSharedPreferencesDAO;

/**
 * Creates the current DAOs implementations.
 * 
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
public class DaoFactory {

    private final Context appContext;

    /**
     * Creates a DAOFactory.
     * 
     * @param context any context.
     */
    public DaoFactory(Context context) {
        appContext = context.getApplicationContext();
    }

    /** @return a new FoursquareAuthDAO. */
    public FoursquareAuthDao getFoursquareAuthDAO() {
        return new FoursquareAuthSharedPreferencesDAO(appContext);
    }
}