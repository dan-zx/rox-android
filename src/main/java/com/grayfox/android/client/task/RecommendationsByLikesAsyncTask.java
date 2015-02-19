package com.grayfox.android.client.task;

import android.content.Context;

import com.grayfox.android.client.RecommendationsApi;
import com.grayfox.android.client.model.Location;
import com.grayfox.android.client.model.Recommendation;
import com.grayfox.android.dao.AccessTokenDao;

import javax.inject.Inject;

public abstract class RecommendationsByLikesAsyncTask extends NetworkAsyncTask<Recommendation[]> {

    @Inject private AccessTokenDao accessTokenDao;
    @Inject private RecommendationsApi recommendationsApi;

    private Location location;
    private Integer radius;
    private RecommendationsApi.Transportation transportation;

    protected RecommendationsByLikesAsyncTask(Context context) {
        super(context);
    }

    public RecommendationsByLikesAsyncTask location(Location location) {
        this.location = location;
        return this;
    }

    public RecommendationsByLikesAsyncTask radius(int radius) {
        this.radius = radius;
        return this;
    }

    public RecommendationsByLikesAsyncTask transportation(RecommendationsApi.Transportation transportation) {
        this.transportation = transportation;
        return this;
    }

    @Override
    public Recommendation[] call() throws Exception {
        return recommendationsApi.awaitRecommendationsByLikes(accessTokenDao.fetchAccessToken(), location, radius, transportation);
    }
}
