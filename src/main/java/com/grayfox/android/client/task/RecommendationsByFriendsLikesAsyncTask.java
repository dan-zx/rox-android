package com.grayfox.android.client.task;

import android.content.Context;

import com.grayfox.android.client.model.Recommendation;

public class RecommendationsByFriendsLikesAsyncTask extends BaseRecommendationAsyncTask {

    protected RecommendationsByFriendsLikesAsyncTask(Context context) {
        super(context);
    }

    @Override
    public Recommendation[] call() throws Exception {
        return getRecommendationsApi().awaitRecommendationsByFriendsLikes(getAccessTokenDao().fetchAccessToken(), getLocation(), getRadius(), getTransportation());
    }
}