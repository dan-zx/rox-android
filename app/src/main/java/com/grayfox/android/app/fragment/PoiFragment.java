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
package com.grayfox.android.app.fragment;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.grayfox.android.app.R;
import com.grayfox.android.app.activity.RecommendedRouteActivity;
import com.grayfox.android.client.model.Poi;

import com.squareup.picasso.Picasso;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class PoiFragment extends RoboFragment {

    private static final String POI_ARG = "POI";
    private static final String LOCATION_ARG = "LOCATION";

    @InjectView(R.id.category_image) private ImageView categoryImageView;
    @InjectView(R.id.category_name)  private TextView categoryNameTextView;
    @InjectView(R.id.build_route)    private Button buildRouteButton;
    @InjectView(R.id.poi_rating)     private TextView poiRatingTextView;
    @InjectView(R.id.poi_name)       private TextView poiNameTextView;

    public static PoiFragment newInstance(Poi poi, Location location) {
        PoiFragment fragment = new PoiFragment();
        Bundle args = new Bundle();
        args.putSerializable(POI_ARG, poi);
        args.putParcelable(LOCATION_ARG, location);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.poi_item, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Poi poi = getPoiArg();
        final Location location = getLocationArg();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_view_fsq_venue, poi.getFoursquareId())));
                startActivity(intent);
            }
        });
        buildRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (location != null)
                    startActivity(RecommendedRouteActivity.getIntent(getActivity(), location, poi));
            }
        });
        categoryNameTextView.setText(poi.getCategories()[0].getName());
        categoryImageView.setBackgroundResource(R.drawable.ic_map_pin_light_blue);
        Picasso.with(getActivity())
                .load(poi.getCategories()[0].getIconUrl())
                .placeholder(R.drawable.ic_generic_category)
                .into(categoryImageView);
        poiNameTextView.setText(poi.getName());
        if (poi.getFoursquareRating() != null) poiRatingTextView.setText(String.valueOf(poi.getFoursquareRating()));
    }

    private Poi getPoiArg() {
        return (Poi) getArguments().getSerializable(POI_ARG);
    }

    private Location getLocationArg() {
        return getArguments().getParcelable(LOCATION_ARG);
    }
}