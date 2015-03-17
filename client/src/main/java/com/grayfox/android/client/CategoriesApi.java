package com.grayfox.android.client;

import android.content.Context;
import android.net.Uri;

import com.grayfox.android.client.model.Category;

public class CategoriesApi extends BaseApi {

    public CategoriesApi(Context context) {
        super(context);
    }

    public Category[] awaitCategoriesLikeName(String partialName) {
        String url = new Uri.Builder().scheme(getString(R.string.gf_api_host_scheme))
                .encodedAuthority(getString(R.string.gf_api_host))
                .appendEncodedPath(getString(R.string.gf_api_path))
                .appendEncodedPath(getString(R.string.gf_api_categories_path))
                .appendEncodedPath(getString(R.string.gf_api_categories_like_path))
                .appendPath(partialName)
                .build().toString();

        return request(url, Category[].class);
    }
}