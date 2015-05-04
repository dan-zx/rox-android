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
import com.grayfox.android.client.model.Recommendation;

import com.squareup.picasso.Picasso;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class RecommendationFragment extends RoboFragment {

    private static final String RECOMMENDATION_ARG = "RECOMMENDATION";
    private static final String LOCATION_ARG = "LOCATION";

    @InjectView(R.id.category_image) private ImageView categoryImageView;
    @InjectView(R.id.build_route)    private Button buildRouteButton;
    @InjectView(R.id.poi_rating)     private TextView poiRatingTextView;
    @InjectView(R.id.poi_name)       private TextView poiNameTextView;
    @InjectView(R.id.reason)         private TextView reasonTextView;

    public static RecommendationFragment newInstance(Recommendation recommendation, Location location) {
        RecommendationFragment fragment = new RecommendationFragment();
        Bundle args = new Bundle();
        args.putSerializable(RECOMMENDATION_ARG, recommendation);
        args.putParcelable(LOCATION_ARG, location);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recommendation_item, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Recommendation recommendation = getRecommendationArg();
        final Location location = getLocationArg();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_view_fsq_venue, recommendation.getPoi().getFoursquareId())));
                startActivity(intent);
            }
        });
        buildRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (location != null) startActivity(RecommendedRouteActivity.getIntent(getActivity(), location, recommendation.getPoi()));
            }
        });
        switch (recommendation.getType()) {
            case GLOBAL:
                categoryImageView.setBackgroundResource(R.drawable.ic_map_pin_light_blue);
                break;
            case SELF:
                categoryImageView.setBackgroundResource(R.drawable.ic_map_pin_pink);
                break;
            case SOCIAL:
                categoryImageView.setBackgroundResource(R.drawable.ic_map_pin_dark_blue);
                break;
        }
        Picasso.with(getActivity())
                .load(recommendation.getPoi().getCategories()[0].getIconUrl())
                .placeholder(R.drawable.ic_generic_category)
                .into(categoryImageView);
        poiNameTextView.setText(recommendation.getPoi().getName());
        if (recommendation.getPoi().getFoursquareRating() != null) poiRatingTextView.setText(String.valueOf(recommendation.getPoi().getFoursquareRating()));
        reasonTextView.setText(recommendation.getReason());
    }

    private Recommendation getRecommendationArg() {
        return (Recommendation) getArguments().getSerializable(RECOMMENDATION_ARG);
    }

    private Location getLocationArg() {
        return getArguments().getParcelable(LOCATION_ARG);
    }
}