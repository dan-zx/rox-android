package com.grayfox.android.client;

import android.content.Context;
import android.net.Uri;

import com.grayfox.android.R;
import com.grayfox.android.client.model.AccessToken;
import com.grayfox.android.client.model.Category;
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
                .appendEncodedPath(getString(R.string.gf_api_users_path))
                .appendEncodedPath(getString(R.string.gf_api_users_register_with_foursquare_path))
                .appendQueryParameter("authorization-code", foursquareAuthorizationCode)
                .build().toString();

        return request(url, AccessToken.class).getToken();
    }

    public User awaitSelfUser(String accessToken) {
        String url = new Uri.Builder().scheme(getString(R.string.gf_api_host_scheme))
                .encodedAuthority(getString(R.string.gf_api_host))
                .appendEncodedPath(getString(R.string.gf_api_path))
                .appendEncodedPath(getString(R.string.gf_api_users_path))
                .appendEncodedPath(getString(R.string.gf_api_users_self_path))
                .appendQueryParameter("access-token", accessToken)
                .build().toString();

        return request(url, User.class);
    }

    public User[] awaitSelfUserFriends(String accessToken) {
        String url = new Uri.Builder().scheme(getString(R.string.gf_api_host_scheme))
                .encodedAuthority(getString(R.string.gf_api_host))
                .appendEncodedPath(getString(R.string.gf_api_path))
                .appendEncodedPath(getString(R.string.gf_api_users_path))
                .appendEncodedPath(getString(R.string.gf_api_users_self_path))
                .appendEncodedPath(getString(R.string.gf_api_users_friends_path))
                .appendQueryParameter("access-token", accessToken)
                .build().toString();

        return request(url, User[].class);
    }

    public Category[] awaitSelfUserLikes(String accessToken) {
        String url = new Uri.Builder().scheme(getString(R.string.gf_api_host_scheme))
                .encodedAuthority(getString(R.string.gf_api_host))
                .appendEncodedPath(getString(R.string.gf_api_path))
                .appendEncodedPath(getString(R.string.gf_api_users_path))
                .appendEncodedPath(getString(R.string.gf_api_users_self_path))
                .appendEncodedPath(getString(R.string.gf_api_users_likes_path))
                .appendQueryParameter("access-token", accessToken)
                .build().toString();

        return request(url, Category[].class);
    }

    public Category[] awaitFriendLikes(String accessToken, String foursquareId) {
        String url = new Uri.Builder().scheme(getString(R.string.gf_api_host_scheme))
                .encodedAuthority(getString(R.string.gf_api_host))
                .appendEncodedPath(getString(R.string.gf_api_path))
                .appendEncodedPath(getString(R.string.gf_api_users_path))
                .appendEncodedPath(getString(R.string.gf_api_users_self_path))
                .appendEncodedPath(getString(R.string.gf_api_users_friend_path))
                .appendEncodedPath(foursquareId)
                .appendEncodedPath(getString(R.string.gf_api_users_likes_path))
                .appendQueryParameter("access-token", accessToken)
                .build().toString();

        return request(url, Category[].class);
    }
}