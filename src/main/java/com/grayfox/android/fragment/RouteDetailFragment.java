package com.grayfox.android.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grayfox.android.R;
import com.grayfox.android.client.model.Recommendation;
import com.grayfox.android.widget.RecommendationAdapter;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class RouteDetailFragment extends RoboFragment {

    private static final String RECOMMENDATION_ARG = "RECOMMENDATION";

    @InjectView(R.id.poi_list) private RecyclerView poiListView;

    public static RouteDetailFragment newInstance(Recommendation recommendation) {
        RouteDetailFragment fragment = new RouteDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(RECOMMENDATION_ARG, recommendation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_route_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        poiListView.setHasFixedSize(true);
        poiListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        poiListView.setAdapter(new RecommendationAdapter(getRecommendation()));
    }

    private Recommendation getRecommendation() {
        return (Recommendation) getArguments().getSerializable(RECOMMENDATION_ARG);
    }
}