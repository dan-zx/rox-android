/*
 * Copyright 2014-2015 Daniel Pedraza-Arcega
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.grayfox.android.app.dao.impl.sharedpreferences;

import android.content.Context;
import android.preference.PreferenceManager;

import com.grayfox.android.app.R;
import com.grayfox.android.app.dao.AccessTokenDao;

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
