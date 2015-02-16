package com.grayfox.android.client;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.grayfox.android.R;
import com.grayfox.android.client.model.ApiResponse;
import com.grayfox.android.client.model.Location;
import com.grayfox.android.client.model.Recommendation;
import com.grayfox.android.http.Charset;
import com.grayfox.android.http.ContentType;
import com.grayfox.android.http.Header;
import com.grayfox.android.http.Method;
import com.grayfox.android.http.RequestBuilder;

import javax.inject.Inject;

public class RecommendationsApi extends BaseApi {

    public static enum Transportation { DRIVING, WALKING, BICYCLING, TRANSIT }

    private static final String TAG = RecommendationsApi.class.getSimpleName();

    @Inject
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

        String json = RequestBuilder.newInstance(url).setMethod(Method.GET)
                .setHeader(Header.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                .setHeader(Header.ACCEPT_LANGUAGE, getClientAcceptLanguage())
                .setHeader(Header.ACCEPT_CHARSET, Charset.UTF_8.getValue())
                .makeForResult();

        if (json != null) {
            ApiResponse<Recommendation[]> apiResponse = parse(json, Recommendation[].class);
            if (apiResponse.getError() == null) return apiResponse.getResponse();
            else {
                Log.e(TAG, "Response error ->" + apiResponse.getError());
                throw new ApiException(apiResponse.getError().getErrorMessage());
            }
        } else {
            Log.e(TAG, "Null response");
            throw new ApiException(getString(R.string.grayfox_api_request_error));
        }
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

        String json = RequestBuilder.newInstance(url).setMethod(Method.GET)
                .setHeader(Header.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                .setHeader(Header.ACCEPT_LANGUAGE, getClientAcceptLanguage())
                .setHeader(Header.ACCEPT_CHARSET, Charset.UTF_8.getValue())
                .makeForResult();

        if (json != null) {
            ApiResponse<Recommendation[]> apiResponse = parse(json, Recommendation[].class);
            if (apiResponse.getError() == null) return apiResponse.getResponse();
            else {
                Log.e(TAG, "Response error ->" + apiResponse.getError());
                throw new ApiException(apiResponse.getError().getErrorMessage());
            }
        } else {
            Log.e(TAG, "Null response");
            throw new ApiException(getString(R.string.grayfox_api_request_error));
        }
    }
}