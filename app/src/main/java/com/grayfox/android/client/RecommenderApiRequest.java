package com.grayfox.android.client;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;

import com.grayfox.android.R;
import com.grayfox.android.client.model.Location;
import com.grayfox.android.client.model.Recommendation;
import com.grayfox.android.http.Charset;
import com.grayfox.android.http.ContentType;
import com.grayfox.android.http.Header;
import com.grayfox.android.http.Method;
import com.grayfox.android.http.RequestBuilder;

public class RecommenderApiRequest extends BaseApiRequest {

    private Transportation transportation;
    private Location location;
    private String category;
    private Integer radius;

    protected RecommenderApiRequest(Context context) {
        super(context);
    }

    @Override
    public RecommenderApiRequest accessToken(String accessToken) {
        super.accessToken(accessToken);
        return this;
    }

    public RecommenderApiRequest transportation(Transportation transportation) {
        this.transportation = transportation;
        return this;
    }

    public RecommenderApiRequest location(Location location) {
        this.location = location;
        return this;
    }

    public RecommenderApiRequest category(String category) {
        this.category = category;
        return this;
    }

    public RecommenderApiRequest radius(int radius) {
        this.radius = radius;
        return this;
    }

    public Recommendation awaitRecommend() {
        String url = new Uri.Builder().scheme(getString(R.string.gf_api_host_scheme))
                .encodedAuthority(getString(R.string.gf_api_host))
                .appendEncodedPath(getString(R.string.gf_api_path))
                .appendEncodedPath(getString(R.string.gf_api_recommendations_path))
                .appendEncodedPath(getString(R.string.gf_api_recommendations_recommend_path))
                .appendQueryParameter("app-access-token", getAccessToken())
                .appendQueryParameter("location", location.stringValues())
                .appendQueryParameter("radius", radius != null ? radius.toString() : null)
                .appendQueryParameter("transportation", transportation.name())
                .build().toString();

        String json = RequestBuilder.newInstance(url).setMethod(Method.GET)
                .setHeader(Header.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                .setHeader(Header.ACCEPT_LANGUAGE, getClientAcceptLanguage())
                .setHeader(Header.ACCEPT_CHARSET, Charset.UTF_8.getValue())
                .makeForResult();

        if (json != null) return new Gson().fromJson(json, Recommendation.class);
        else return null;
    }

    public Recommendation awaitSearch() {
        String url = new Uri.Builder().scheme(getString(R.string.gf_api_host_scheme))
                .encodedAuthority(getString(R.string.gf_api_host))
                .appendEncodedPath(getString(R.string.gf_api_path))
                .appendEncodedPath(getString(R.string.gf_api_recommendations_path))
                .appendEncodedPath(getString(R.string.gf_api_recommendations_search_path))
                .appendQueryParameter("app-access-token", getAccessToken())
                .appendQueryParameter("location", location.stringValues())
                .appendQueryParameter("radius", radius != null ? radius.toString() : null)
                .appendQueryParameter("transportation", transportation.name())
                .appendQueryParameter("category", category)
                .build().toString();

        String json = RequestBuilder.newInstance(url).setMethod(Method.GET)
                .setHeader(Header.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                .setHeader(Header.ACCEPT_LANGUAGE, getClientAcceptLanguage())
                .setHeader(Header.ACCEPT_CHARSET, Charset.UTF_8.getValue())
                .makeForResult();

        if (json != null) return new Gson().fromJson(json, Recommendation.class);
        else return null;
    }

    public static enum Transportation { DRIVING, WALKING, BICYCLING }
}