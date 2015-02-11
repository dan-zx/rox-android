package com.grayfox.android.client;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.grayfox.android.R;
import com.grayfox.android.client.model.AccessToken;
import com.grayfox.android.client.model.Result;
import com.grayfox.android.client.model.User;
import com.grayfox.android.http.Charset;
import com.grayfox.android.http.ContentType;
import com.grayfox.android.http.Header;
import com.grayfox.android.http.Method;
import com.grayfox.android.http.RequestBuilder;

import javax.inject.Inject;

public class UsersApi extends BaseApi {

    private static final String TAG = UsersApi.class.getSimpleName();

    @Inject
    public UsersApi(Context context) {
        super(context);
    }

    public String awaitAccessToken(String foursquareAuthorizationCode) {
        String url = new Uri.Builder().scheme(getString(R.string.gf_api_host_scheme))
                .encodedAuthority(getString(R.string.gf_api_host))
                .appendEncodedPath(getString(R.string.gf_api_path))
                .appendEncodedPath(getString(R.string.gf_api_app_users_path))
                .appendEncodedPath(getString(R.string.gf_api_app_users_register_with_foursquare_path))
                .appendQueryParameter("foursquare-authorization-code", foursquareAuthorizationCode)
                .build().toString();

        String json = new RequestBuilder(url).setMethod(Method.GET)
                .setHeader(Header.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                .setHeader(Header.ACCEPT_LANGUAGE, getClientAcceptLanguage())
                .setHeader(Header.ACCEPT_CHARSET, Charset.UTF_8.getValue())
                .makeForResult();

        if (json != null) {
            Result<AccessToken> result = parse(json, AccessToken.class);
            if (result.getError() == null) return result.getResponse().getToken();
            else {
                Log.e(TAG, "Response error ->" + result.getError());
                throw new ApiException(result.getError().getErrorMessage());
            }
        } else {
            Log.e(TAG, "Null response");
            throw new ApiException(getString(R.string.grayfox_api_request_error));
        }
    }

    public User awaitSelfUser(String accessToken) {
        String url = new Uri.Builder().scheme(getString(R.string.gf_api_host_scheme))
                .encodedAuthority(getString(R.string.gf_api_host))
                .appendEncodedPath(getString(R.string.gf_api_path))
                .appendEncodedPath(getString(R.string.gf_api_app_users_path))
                .appendEncodedPath(getString(R.string.gf_api_app_users_self_path))
                .appendQueryParameter("access-token", accessToken)
                .build().toString();

        String json = new RequestBuilder(url).setMethod(Method.GET)
                .setHeader(Header.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                .setHeader(Header.ACCEPT_LANGUAGE, getClientAcceptLanguage())
                .setHeader(Header.ACCEPT_CHARSET, Charset.UTF_8.getValue())
                .makeForResult();

        if (json != null) {
            Result<User> result = parse(json, User.class);
            if (result.getError() == null) return result.getResponse();
            else {
                Log.e(TAG, "Response error ->" + result.getError());
                throw new ApiException(result.getError().getErrorMessage());
            }
        } else {
            Log.e(TAG, "Null response");
            throw new ApiException(getString(R.string.grayfox_api_request_error));
        }
    }
}