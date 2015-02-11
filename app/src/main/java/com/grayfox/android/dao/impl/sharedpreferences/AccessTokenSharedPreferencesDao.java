package com.grayfox.android.dao.impl.sharedpreferences;

import android.content.Context;
import android.preference.PreferenceManager;

import com.grayfox.android.R;
import com.grayfox.android.dao.AccessTokenDao;

import javax.inject.Inject;

public class AccessTokenSharedPreferencesDao implements AccessTokenDao {

    private final Context context;

    @Inject
    public AccessTokenSharedPreferencesDao(Context context) {
        this.context = context;
    }

    @Override
    public String fetchAccessToken() {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.access_token_key), null);
    }

    @Override
    public void saveOrUpdateAccessToken(String accessToken) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(context.getString(R.string.access_token_key), accessToken)
                .commit();
    }

    @Override
    public void deleteAccessToken() {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .remove(context.getString(R.string.access_token_key))
                .commit();
    }
}
