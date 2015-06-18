/*
 * Copyright 2014-2015 Daniel Pedraza-Arcega
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.grayfox.android.client;

import android.content.Context;
import android.net.Uri;

import com.grayfox.android.client.model.Category;
import com.grayfox.android.client.model.Location;
import com.grayfox.android.client.model.Poi;
import com.grayfox.android.client.model.Recommendation;

public class PoisApi extends BaseApi {

    public PoisApi(Context context) {
        super(context);
    }

    public Poi[] awaitSearchByCategory(Location location, Integer radius, String categoryFoursquareId) {
        Uri.Builder uriBuilder = Uri.parse(getString(R.string.gf_api_base_url)).buildUpon()
                .appendEncodedPath(getString(R.string.gf_api_pois_path))
                .appendEncodedPath(getString(R.string.gf_api_pois_search_path));
        if (location != null) uriBuilder.appendQueryParameter("location", location.stringValues());
        if (radius != null) uriBuilder.appendQueryParameter("radius", radius.toString());
        if (categoryFoursquareId != null) uriBuilder.appendQueryParameter("category_foursquare_id", categoryFoursquareId);

        return get(uriBuilder.build().toString(), Poi[].class);
    }

    public Poi[] awaitRoute(String poiFoursquareId) {
        Uri.Builder uriBuilder = Uri.parse(getString(R.string.gf_api_base_url)).buildUpon()
                .appendEncodedPath(getString(R.string.gf_api_pois_path))
                .appendEncodedPath(getString(R.string.gf_api_pois_route_path));
        if (poiFoursquareId != null) uriBuilder.appendQueryParameter("poi_foursquare_id", poiFoursquareId);

        return get(uriBuilder.build().toString(), Poi[].class);
    }

    public Category[] awaitCategoriesLikeName(String partialName) {
        Uri.Builder uriBuilder = Uri.parse(getString(R.string.gf_api_base_url)).buildUpon()
                .appendEncodedPath(getString(R.string.gf_api_pois_path))
                .appendEncodedPath(getString(R.string.gf_api_pois_categories_like_path));
        if (partialName != null) uriBuilder.appendPath(partialName);

        return get(uriBuilder.build().toString(), Category[].class);
    }

    public Recommendation[] awaitRecommendations(String accessToken, Location location, Integer radius) {
        Uri.Builder uriBuilder = Uri.parse(getString(R.string.gf_api_base_url)).buildUpon()
                .appendEncodedPath(getString(R.string.gf_api_pois_path))
                .appendEncodedPath(getString(R.string.gf_api_pois_recommend_path));
        if (accessToken != null) uriBuilder.appendQueryParameter("access_token", accessToken);
        if (location != null) uriBuilder.appendQueryParameter("location", location.stringValues());
        if (radius != null) uriBuilder.appendQueryParameter("radius", radius.toString());

        return get(uriBuilder.build().toString(), Recommendation[].class);
    }
}