package com.grayfox.android.dao.impl.sharedpreferences;

import android.content.Context;
import android.preference.PreferenceManager;

import com.grayfox.android.R;
import com.grayfox.android.dao.AppAccessTokenDao;

import javax.inject.Inject;

public class AppAccessTokenSharedPreferencesDao implements AppAccessTokenDao {

    private final Context context;

    @Inject
    public AppAccessTokenSharedPreferencesDao(Context context) {
        this.context = context;
    }

    @Override
    public String fetchAccessToken() {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.app_access_token_key), null);
    }

    @Override
    public void saveOrUpdateAccessToken(String accessToken) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(context.getString(R.string.app_access_token_key), accessToken)
                .commit();
    }

    @Override
    public void deleteAccessToken() {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .remove(context.getString(R.string.app_access_token_key))
                .commit();
    }
}
