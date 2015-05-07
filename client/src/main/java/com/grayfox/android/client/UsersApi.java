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
        Uri.Builder uriBuilder = Uri.parse(getString(R.string.gf_api_base_url)).buildUpon()
                .appendEncodedPath(getString(R.string.gf_api_users_path))
                .appendEncodedPath(getString(R.string.gf_api_users_register_with_foursquare_path));
        if (foursquareAuthorizationCode != null) uriBuilder.appendQueryParameter("authorization_code", foursquareAuthorizationCode);

        return get(uriBuilder.build().toString(), AccessToken.class).getToken();
    }

    public User awaitSelfUser(String accessToken) {
        Uri.Builder uriBuilder = Uri.parse(getString(R.string.gf_api_base_url)).buildUpon()
                .appendEncodedPath(getString(R.string.gf_api_users_path))
                .appendEncodedPath(getString(R.string.gf_api_users_self_path));
        if (accessToken != null) uriBuilder.appendQueryParameter("access_token", accessToken);

        return get(uriBuilder.build().toString(), User.class);
    }

    public User[] awaitSelfUserFriends(String accessToken) {
        Uri.Builder uriBuilder = Uri.parse(getString(R.string.gf_api_base_url)).buildUpon()
                .appendEncodedPath(getString(R.string.gf_api_users_path))
                .appendEncodedPath(getString(R.string.gf_api_users_self_path))
                .appendEncodedPath(getString(R.string.gf_api_users_friends_path));
        if (accessToken != null) uriBuilder.appendQueryParameter("access_token", accessToken);

        return get(uriBuilder.build().toString(), User[].class);
    }

    public Category[] awaitUserLikes(String accessToken, String userFoursquareId) {
        Uri.Builder uriBuilder = Uri.parse(getString(R.string.gf_api_base_url)).buildUpon()
                .appendEncodedPath(getString(R.string.gf_api_users_path));
        if (userFoursquareId != null) uriBuilder.appendPath(userFoursquareId);
        uriBuilder.appendEncodedPath(getString(R.string.gf_api_users_likes_path));
        if (accessToken != null) uriBuilder.appendQueryParameter("access_token", accessToken);

        return get(uriBuilder.build().toString(), Category[].class);
    }

    public UpdateResponse awaitAddLike(String accessToken, String categoryFoursquareId) {
        Uri.Builder uriBuilder = Uri.parse(getString(R.string.gf_api_base_url)).buildUpon()
                .appendEncodedPath(getString(R.string.gf_api_users_path))
                .appendEncodedPath(getString(R.string.gf_api_users_self_path))
                .appendEncodedPath(getString(R.string.gf_api_users_update_path))
                .appendEncodedPath(getString(R.string.gf_api_users_add_like_path));
        if (accessToken != null) uriBuilder.appendQueryParameter("access_token", accessToken);
        if (categoryFoursquareId != null) uriBuilder.appendQueryParameter("category_foursquare_id", categoryFoursquareId);

        return put(uriBuilder.build().toString(), null, UpdateResponse.class);
    }

    public UpdateResponse awaitRemoveLike(String accessToken, String categoryFoursquareId) {
        Uri.Builder uriBuilder = Uri.parse(getString(R.string.gf_api_base_url)).buildUpon()
                .appendEncodedPath(getString(R.string.gf_api_users_path))
                .appendEncodedPath(getString(R.string.gf_api_users_self_path))
                .appendEncodedPath(getString(R.string.gf_api_users_update_path))
                .appendEncodedPath(getString(R.string.gf_api_users_remove_like_path));
        if (accessToken != null) uriBuilder.appendQueryParameter("access_token", accessToken);
        if (categoryFoursquareId != null) uriBuilder.appendQueryParameter("category_foursquare_id", categoryFoursquareId);

        return delete(uriBuilder.build().toString(), null, UpdateResponse.class);
    }
}