package com.grayfox.android.client;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;

import com.grayfox.android.R;
import com.grayfox.android.client.model.AccessToken;
import com.grayfox.android.http.Charset;
import com.grayfox.android.http.ContentType;
import com.grayfox.android.http.Header;
import com.grayfox.android.http.Method;
import com.grayfox.android.http.RequestBuilder;

import javax.inject.Inject;

public class AppUsersApi extends BaseApi {

    @Inject
    public AppUsersApi(Context context) {
        super(context);
    }

    public String awaitAccessToken(String foursquareAuthorizationCode) {
        String url = new Uri.Builder().scheme(getString(R.string.gf_api_host_scheme))
                .encodedAuthority(getString(R.string.gf_api_host))
                .appendEncodedPath(getString(R.string.gf_api_path))
                .appendEncodedPath(getString(R.string.gf_api_app_users_path))
                .appendEncodedPath(getString(R.string.gf_api_app_users_register_path))
                .appendQueryParameter("foursquare-authorization-code", foursquareAuthorizationCode)
                .build().toString();

        String json = RequestBuilder.newInstance(url).setMethod(Method.GET)
                .setHeader(Header.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                .setHeader(Header.ACCEPT_LANGUAGE, getClientAcceptLanguage())
                .setHeader(Header.ACCEPT_CHARSET, Charset.UTF_8.getValue())
                .makeForResult();

        if (json != null) return new Gson().fromJson(json, AccessToken.class).getToken();
        else return null;
    }
}