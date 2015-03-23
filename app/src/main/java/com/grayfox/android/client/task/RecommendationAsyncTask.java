package com.grayfox.android.client.task;

import android.content.Context;

import com.grayfox.android.app.dao.AccessTokenDao;
import com.grayfox.android.client.RecommendationsApi;
import com.grayfox.android.client.model.Location;
import com.grayfox.android.client.model.Recommendation;

import javax.inject.Inject;

public abstract class RecommendationAsyncTask extends NetworkAsyncTask<Recommendation[]> {

    @Inject private AccessTokenDao accessTokenDao;
    @Inject private RecommendationsApi recommendationsApi;

    private Location location;
    private Integer radius;

    protected RecommendationAsyncTask(Context context) {
        super(context);
    }

    public RecommendationAsyncTask location(Location location) {
        this.location = location;
        return this;
    }

    public RecommendationAsyncTask radius(int radius) {
        this.radius = radius;
        return this;
    }

    @Override
    public Recommendation[] call() throws Exception {
        return recommendationsApi.awaitRecommendationsByAll(accessTokenDao.fetchAccessToken(), location, radius);
    }
}