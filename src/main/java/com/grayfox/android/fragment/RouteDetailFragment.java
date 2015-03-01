package com.grayfox.android.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grayfox.android.R;
import com.grayfox.android.activity.RouteDisplayingActivity;
import com.grayfox.android.client.model.Location;
import com.grayfox.android.client.model.Recommendation;
import com.grayfox.android.widget.RecommendationAdapter;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class RouteDetailFragment extends RoboFragment {

    private static final String RECOMMENDATION_ARG = "RECOMMENDATION";
    private static final String ORIGIN_LOCATION_ARG = "ORIGIN_LOCATION";
    private static final String ORIGIN_ADDRESS_ARG = "ORIGIN_ADDRESS";

    @InjectView(R.id.recommendation_reason) private TextView recommendationReasonView;
    @InjectView(R.id.poi_list)              private RecyclerView poiListView;

    public static RouteDetailFragment newInstance(Location originLocation, String originAddress, Recommendation recommendation) {
        RouteDetailFragment fragment = new RouteDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ORIGIN_LOCATION_ARG, originLocation);
        args.putString(ORIGIN_ADDRESS_ARG, originAddress);
        args.putSerializable(RECOMMENDATION_ARG, recommendation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_route_detail, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.route_detail, menu);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //recommendationReasonView.setText(getRecommendation().getReason());
        recommendationReasonView.setVisibility(View.GONE);
        poiListView.setHasFixedSize(true);
        poiListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        poiListView.setAdapter(new RecommendationAdapter(getOriginAddress(), getRecommendation()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_view_in_map:
                startActivity(RouteDisplayingActivity.getIntent(getActivity(), getOriginLocation(), getRecommendation()));
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private Location getOriginLocation() {
        return (Location) getArguments().getSerializable(ORIGIN_LOCATION_ARG);
    }

    private String getOriginAddress() {
        return getArguments().getString(ORIGIN_ADDRESS_ARG);
    }

    private Recommendation getRecommendation() {
        return (Recommendation) getArguments().getSerializable(RECOMMENDATION_ARG);
    }
}