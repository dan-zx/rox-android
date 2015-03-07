package com.grayfox.android.client;

import android.content.Context;
import android.net.Uri;

import com.grayfox.android.client.model.Location;
import com.grayfox.android.client.model.Recommendation;

public class RecommendationsApi extends BaseApi {

    public static enum Transportation {DRIVING, WALKING, BICYCLING, TRANSIT}

    public RecommendationsApi(Context context) {
        super(context);
    }

    public Recommendation[] awaitRecommendationsByLikes(String accessToken, Location location, Integer radius, Transportation transportation) {
        String url = new Uri.Builder().scheme(getString(R.string.gf_api_host_scheme))
                .encodedAuthority(getString(R.string.gf_api_host))
                .appendEncodedPath(getString(R.string.gf_api_path))
                .appendEncodedPath(getString(R.string.gf_api_recommendations_path))
                .appendEncodedPath(getString(R.string.gf_api_recommendations_by_likes_path))
                .appendQueryParameter("access-token", accessToken)
                .appendQueryParameter("location", location != null ? location.stringValues() : null)
                .appendQueryParameter("radius", radius != null ? radius.toString() : null)
                .appendQueryParameter("transportation", transportation != null ? transportation.name() : null)
                .build().toString();

        return request(url, Recommendation[].class);
    }

    public Recommendation[] awaitRecommendationsByFriendsLikes(String accessToken, Location location, Integer radius, Transportation transportation) {
        String url = new Uri.Builder().scheme(getString(R.string.gf_api_host_scheme))
                .encodedAuthority(getString(R.string.gf_api_host))
                .appendEncodedPath(getString(R.string.gf_api_path))
                .appendEncodedPath(getString(R.string.gf_api_recommendations_path))
                .appendEncodedPath(getString(R.string.gf_api_recommendations_by_friends_likes_path))
                .appendQueryParameter("access-token", accessToken)
                .appendQueryParameter("location", location != null ? location.stringValues() : null)
                .appendQueryParameter("radius", radius != null ? radius.toString() : null)
                .appendQueryParameter("transportation", transportation != null ? transportation.name() : null)
                .build().toString();

        return request(url, Recommendation[].class);
    }
}