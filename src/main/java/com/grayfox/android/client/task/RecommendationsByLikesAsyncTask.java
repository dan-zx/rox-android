package com.grayfox.android.client.task;

import android.content.Context;

import com.grayfox.android.client.model.Recommendation;

public abstract class RecommendationsByLikesAsyncTask extends BaseRecommendationAsyncTask {

    protected RecommendationsByLikesAsyncTask(Context context) {
        super(context);
    }

    @Override
    public Recommendation[] call() throws Exception {
        return getRecommendationsApi().awaitRecommendationsByLikes(getAccessTokenDao().fetchAccessToken(), getLocation(), getRadius(), getTransportation());
    }
}