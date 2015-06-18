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
package com.grayfox.android.app.config;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.google.maps.GeoApiContext;

import com.grayfox.android.app.R;
import com.grayfox.android.app.dao.source.sqlite.GrayFoxDatabaseHelper;
import com.grayfox.android.client.PoisApi;
import com.grayfox.android.client.UsersApi;

public class ConfigModule extends AbstractModule {

    private final Application context;

    public ConfigModule(Application context) {
        this.context = context;
    }

    @Override
    protected void configure() {
        bind(UsersApi.class)
                .toInstance(new UsersApi(context));
        bind(PoisApi.class)
                .toInstance(new PoisApi(context));
        bind(SQLiteOpenHelper.class)
                .annotatedWith(Names.named("GrayFoxDbHelper"))
                .toInstance(new GrayFoxDatabaseHelper(context));
        bind(GeoApiContext.class)
                .toInstance(new GeoApiContext().setApiKey(context.getString(R.string.google_maps_services_key)));
    }
}