package com.grayfox.android.client.task;

import android.content.Context;

import com.grayfox.android.app.dao.AccessTokenDao;
import com.grayfox.android.client.RecommendationsApi;
import com.grayfox.android.client.model.Location;
import com.grayfox.android.client.model.Recommendation;

import javax.inject.Inject;

public abstract class BaseRecommendationAsyncTask extends NetworkAsyncTask<Recommendation[]> {

    @Inject private AccessTokenDao accessTokenDao;
    @Inject private RecommendationsApi recommendationsApi;

    private Location location;
    private Integer radius;
    private RecommendationsApi.Transportation transportation;

    protected BaseRecommendationAsyncTask(Context context) {
        super(context);
    }

    public BaseRecommendationAsyncTask location(Location location) {
        this.location = location;
        return this;
    }

    public BaseRecommendationAsyncTask radius(int radius) {
        this.radius = radius;
        return this;
    }

    public BaseRecommendationAsyncTask transportation(RecommendationsApi.Transportation transportation) {
        this.transportation = transportation;
        return this;
    }

    protected AccessTokenDao getAccessTokenDao() {
        return accessTokenDao;
    }

    protected RecommendationsApi getRecommendationsApi() {
        return recommendationsApi;
    }

    protected Location getLocation() {
        return location;
    }

    protected Integer getRadius() {
        return radius;
    }

    protected RecommendationsApi.Transportation getTransportation() {
        return transportation;
    }
}