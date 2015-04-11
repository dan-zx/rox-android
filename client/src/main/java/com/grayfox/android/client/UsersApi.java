package com.grayfox.android.client;

import android.content.Context;
import android.net.Uri;

import com.grayfox.android.client.model.AccessToken;
import com.grayfox.android.client.model.Category;
import com.grayfox.android.client.model.UpdateResponse;
import com.grayfox.android.client.model.User;

public class UsersApi extends BaseApi {

    public UsersApi(Context context) {
        super(context);
    }

    public String awaitAccessToken(String foursquareAuthorizationCode) {
        String url = Uri.parse(getString(R.string.gf_api_base_url)).buildUpon()
                .appendEncodedPath(getString(R.string.gf_api_users_path))
                .appendEncodedPath(getString(R.string.gf_api_users_register_with_foursquare_path))
                .appendQueryParameter("authorization-code", foursquareAuthorizationCode)
                .build().toString();

        return get(url, AccessToken.class).getToken();
    }

    public User awaitSelfUser(String accessToken) {
        String url = Uri.parse(getString(R.string.gf_api_base_url)).buildUpon()
                .appendEncodedPath(getString(R.string.gf_api_users_path))
                .appendEncodedPath(getString(R.string.gf_api_users_self_path))
                .appendQueryParameter("access-token", accessToken)
                .build().toString();

        return get(url, User.class);
    }

    public User[] awaitSelfUserFriends(String accessToken) {
        String url = Uri.parse(getString(R.string.gf_api_base_url)).buildUpon()
                .appendEncodedPath(getString(R.string.gf_api_users_path))
                .appendEncodedPath(getString(R.string.gf_api_users_self_path))
                .appendEncodedPath(getString(R.string.gf_api_users_friends_path))
                .appendQueryParameter("access-token", accessToken)
                .build().toString();

        return get(url, User[].class);
    }

    public Category[] awaitSelfUserLikes(String accessToken) {
        String url = Uri.parse(getString(R.string.gf_api_base_url)).buildUpon()
                .appendEncodedPath(getString(R.string.gf_api_users_path))
                .appendEncodedPath(getString(R.string.gf_api_users_self_path))
                .appendEncodedPath(getString(R.string.gf_api_users_likes_path))
                .appendQueryParameter("access-token", accessToken)
                .build().toString();

        return get(url, Category[].class);
    }

    public Category[] awaitUserLikes(String accessToken, String foursquareId) {
        String url = Uri.parse(getString(R.string.gf_api_base_url)).buildUpon()
                .appendEncodedPath(getString(R.string.gf_api_users_path))
                .appendPath(foursquareId)
                .appendEncodedPath(getString(R.string.gf_api_users_likes_path))
                .appendQueryParameter("access-token", accessToken)
                .build().toString();

        return get(url, Category[].class);
    }

    public UpdateResponse awaitAddLike(String accessToken, Category like) {
        String url = Uri.parse(getString(R.string.gf_api_base_url)).buildUpon()
                .appendEncodedPath(getString(R.string.gf_api_users_path))
                .appendEncodedPath(getString(R.string.gf_api_users_self_path))
                .appendEncodedPath(getString(R.string.gf_api_users_update_path))
                .appendEncodedPath(getString(R.string.gf_api_users_add_like_path))
                .appendQueryParameter("access-token", accessToken)
                .build().toString();

        return put(url, like, UpdateResponse.class);
    }

    public UpdateResponse awaitRemoveLike(String accessToken, Category like) {
        String url = Uri.parse(getString(R.string.gf_api_base_url)).buildUpon()
                .appendEncodedPath(getString(R.string.gf_api_users_path))
                .appendEncodedPath(getString(R.string.gf_api_users_self_path))
                .appendEncodedPath(getString(R.string.gf_api_users_update_path))
                .appendEncodedPath(getString(R.string.gf_api_users_remove_like_path))
                .appendQueryParameter("access-token", accessToken)
                .build().toString();

        return delete(url, like, UpdateResponse.class);
    }
}