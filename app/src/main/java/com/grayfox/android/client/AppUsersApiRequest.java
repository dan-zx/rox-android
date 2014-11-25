package com.grayfox.android.client;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.gson.Gson;

import com.grayfox.android.R;
import com.grayfox.android.client.model.AccessToken;
import com.grayfox.android.dao.AppAccessTokenDao;
import com.grayfox.android.http.Charset;
import com.grayfox.android.http.ContentType;
import com.grayfox.android.http.Header;
import com.grayfox.android.http.Method;
import com.grayfox.android.http.RequestBuilder;

import javax.inject.Inject;

public class AppUsersApiRequest extends BaseApiRequest {

    private final AppAccessTokenDao appAccessTokenDao;

    private String foursquareAuthorizationCode;

    @Inject
    public AppUsersApiRequest(Context context, AppAccessTokenDao appAccessTokenDao) {
        super(context);
        this.appAccessTokenDao = appAccessTokenDao;
    }

    public AppUsersApiRequest foursquareAuthorizationCode(String foursquareAuthorizationCode) {
        this.foursquareAuthorizationCode = foursquareAuthorizationCode;
        return this;
    }

    public void asyncAccessToken(final RequestCallback<String> callback) {
        if (isConnected()) {
            new AsyncTask<Void, Void, String>() {

                @Override
                protected String doInBackground(Void... voids) {
                    return awaitAccessToken();
                }

                @Override
                protected void onPostExecute(String accessToken) {
                    if (callback != null) {
                        if (accessToken != null) callback.onSuccess(accessToken);
                        else callback.onFailure(getString(R.string.grayfox_api_request_error));
                    }
                }
            }.execute();
        } else {
            if (callback != null) callback.onFailure(getString(R.string.network_unavailable));
        }
    }

    public String awaitAccessToken() {
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

        if (json != null) {
            String accessToken = new Gson().fromJson(json, AccessToken.class).getToken();
            appAccessTokenDao.saveAccessToken(accessToken);
            return accessToken;
        }
        else return null;
    }
}