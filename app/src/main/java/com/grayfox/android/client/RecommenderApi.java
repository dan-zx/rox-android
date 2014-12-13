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

import javax.inject.Inject;

public class RecommenderApi extends BaseApi {

    @Inject
    public RecommenderApi(Context context) {
        super(context);
    }

    public Recommendation awaitSearch(String appAccessToken, Location location, Integer radius, Transportation transportation, String category) {
        String url = new Uri.Builder().scheme(getString(R.string.gf_api_host_scheme))
                .encodedAuthority(getString(R.string.gf_api_host))
                .appendEncodedPath(getString(R.string.gf_api_path))
                .appendEncodedPath(getString(R.string.gf_api_recommendations_path))
                .appendEncodedPath(getString(R.string.gf_api_recommendations_search_path))
                .appendQueryParameter("app-access-token", appAccessToken)
                .appendQueryParameter("location", location != null ? location.stringValues() : null)
                .appendQueryParameter("radius", radius != null ? radius.toString() : null)
                .appendQueryParameter("transportation", transportation != null ? transportation.name() : null)
                .appendQueryParameter("category", category)
                .build().toString();

        String json = new RequestBuilder(url).setMethod(Method.GET)
                .setHeader(Header.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                .setHeader(Header.ACCEPT_LANGUAGE, getClientAcceptLanguage())
                .setHeader(Header.ACCEPT_CHARSET, Charset.UTF_8.getValue())
                .makeForResult();

        if (json != null) return new Gson().fromJson(json, Recommendation.class);
        else return null;
    }

    public static enum Transportation { DRIVING, WALKING, BICYCLING }
}