package com.grayfox.android.config;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import com.grayfox.android.client.UsersApi;
import com.grayfox.android.client.RecommendationsApi;
import com.grayfox.android.dao.source.sqlite.GrayFoxDatabaseHelper;

public class ConfigModule extends AbstractModule {

    private final Application context;

    public ConfigModule(Application context) {
        this.context = context;
    }

    @Override
    protected void configure() {
        bind(UsersApi.class);
        bind(RecommendationsApi.class);
        bind(SQLiteOpenHelper.class)
                .annotatedWith(Names.named("GrayFoxDbHelper"))
                .toInstance(new GrayFoxDatabaseHelper(context));
    }
}