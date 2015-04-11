package com.grayfox.android.client;

import android.content.Context;
import android.net.Uri;

import com.grayfox.android.client.model.Category;
import com.grayfox.android.client.model.Location;
import com.grayfox.android.client.model.Poi;

public class PoisApi extends BaseApi {

    public PoisApi(Context context) {
        super(context);
    }

    public Poi[] awaitSearchByCategory(Location location, Integer radius, String categoryFoursquareId) {
        String url = Uri.parse(getString(R.string.gf_api_base_url)).buildUpon()
                .appendEncodedPath(getString(R.string.gf_api_pois_path))
                .appendEncodedPath(getString(R.string.gf_api_pois_search_path))
                .appendQueryParameter("location", location != null ? location.stringValues() : null)
                .appendQueryParameter("radius", radius != null ? radius.toString() : null)
                .appendQueryParameter("category-foursquare-id", categoryFoursquareId)
                .build().toString();

        return get(url, Poi[].class);
    }

    public Poi[] awaitNextPois(Poi seed) {
        String url = Uri.parse(getString(R.string.gf_api_base_url)).buildUpon()
                .appendEncodedPath(getString(R.string.gf_api_pois_path))
                .appendEncodedPath(getString(R.string.gf_api_pois_next_path))
                .build().toString();

        return post(url, seed, Poi[].class);
    }

    public Category[] awaitCategoriesLikeName(String partialName) {
        String url = Uri.parse(getString(R.string.gf_api_base_url)).buildUpon()
                .appendEncodedPath(getString(R.string.gf_api_pois_path))
                .appendEncodedPath(getString(R.string.gf_api_categories_like_path))
                .appendPath(partialName)
                .build().toString();

        return get(url, Category[].class);
    }
}