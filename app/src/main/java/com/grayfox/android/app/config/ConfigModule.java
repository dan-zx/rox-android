package com.grayfox.android.app.config;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.google.maps.GeoApiContext;

import com.grayfox.android.app.R;
import com.grayfox.android.app.dao.source.sqlite.GrayFoxDatabaseHelper;
import com.grayfox.android.client.CategoriesApi;
import com.grayfox.android.client.PoisApi;
import com.grayfox.android.client.RecommendationsApi;
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
        bind(RecommendationsApi.class)
                .toInstance(new RecommendationsApi(context));
        bind(CategoriesApi.class)
                .toInstance(new CategoriesApi(context));
        bind(PoisApi.class)
                .toInstance(new PoisApi(context));
        bind(SQLiteOpenHelper.class)
                .annotatedWith(Names.named("GrayFoxDbHelper"))
                .toInstance(new GrayFoxDatabaseHelper(context));
        bind(GeoApiContext.class)
                .toInstance(new GeoApiContext().setApiKey(context.getString(R.string.google_maps_services_key)));
    }
}