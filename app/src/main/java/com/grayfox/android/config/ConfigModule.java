package com.grayfox.android.config;

import android.app.Application;

import com.google.inject.AbstractModule;

import com.grayfox.android.client.AppUsersApiRequest;
import com.grayfox.android.client.RecommenderApiRequest;

public class ConfigModule extends AbstractModule {

    private final Application context;

    public ConfigModule(Application context) {
        this.context = context;
    }

    @Override
    protected void configure() {
        bind(AppUsersApiRequest.class);
        bind(RecommenderApiRequest.class);
    }
}