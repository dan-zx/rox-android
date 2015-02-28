package com.grayfox.android.client;

import android.content.Context;
import android.net.Uri;

import com.grayfox.android.R;
import com.grayfox.android.client.model.AccessToken;
import com.grayfox.android.client.model.User;

import javax.inject.Inject;

public class UsersApi extends BaseApi {

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
                .appendQueryParameter("authorization-code", foursquareAuthorizationCode)
                .build().toString();

        return request(url, AccessToken.class).getToken();
    }

    public User awaitSelfUser(String accessToken) {
        String url = new Uri.Builder().scheme(getString(R.string.gf_api_host_scheme))
                .encodedAuthority(getString(R.string.gf_api_host))
                .appendEncodedPath(getString(R.string.gf_api_path))
                .appendEncodedPath(getString(R.string.gf_api_app_users_path))
                .appendEncodedPath(getString(R.string.gf_api_app_users_self_path))
                .appendQueryParameter("access-token", accessToken)
                .build().toString();

        return request(url, User.class);
    }
}