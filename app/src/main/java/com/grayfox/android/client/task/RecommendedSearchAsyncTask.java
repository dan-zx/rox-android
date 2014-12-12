package com.grayfox.android.client.task;

import android.content.Context;

import com.grayfox.android.client.RecommenderApi;
import com.grayfox.android.client.model.Location;
import com.grayfox.android.client.model.Recommendation;
import com.grayfox.android.dao.AppAccessTokenDao;

import javax.inject.Inject;

public abstract class RecommendedSearchAsyncTask extends NetworkAsyncTask<Recommendation> {

    @Inject private AppAccessTokenDao appAccessTokenDao;
    @Inject private RecommenderApi recommenderApi;

    private Location location;
    private Integer radius;
    private RecommenderApi.Transportation transportation;
    private String category;

    protected RecommendedSearchAsyncTask(Context context) {
        super(context);
    }

    public RecommendedSearchAsyncTask location(Location location) {
        this.location = location;
        return this;
    }

    public RecommendedSearchAsyncTask radius(int radius) {
        this.radius = radius;
        return this;
    }

    public RecommendedSearchAsyncTask transportation(RecommenderApi.Transportation transportation) {
        this.transportation = transportation;
        return this;
    }

    public RecommendedSearchAsyncTask category(String category) {
        this.category = category;
        return this;
    }

    @Override
    public Recommendation call() throws Exception {
        return recommenderApi.awaitSearch(appAccessTokenDao.fetchAccessToken(), location, radius, transportation, category);
    }
}