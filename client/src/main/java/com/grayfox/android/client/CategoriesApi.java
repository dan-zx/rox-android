package com.grayfox.android.client;

import android.content.Context;
import android.net.Uri;

import com.grayfox.android.client.model.Category;

public class CategoriesApi extends BaseApi {

    public CategoriesApi(Context context) {
        super(context);
    }

    public Category[] awaitCategoriesLikeName(String partialName) {
        String url = Uri.parse(getString(R.string.gf_api_base_url)).buildUpon()
                .appendEncodedPath(getString(R.string.gf_api_categories_path))
                .appendEncodedPath(getString(R.string.gf_api_categories_like_path))
                .appendPath(partialName)
                .build().toString();

        return get(url, Category[].class);
    }
}