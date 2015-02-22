package com.grayfox.android.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.grayfox.android.R;
import com.grayfox.android.client.RecommendationsApi;
import com.grayfox.android.client.model.Location;
import com.grayfox.android.client.model.Recommendation;
import com.grayfox.android.client.task.RecommendationsByFriendsLikesAsyncTask;
import com.grayfox.android.location.LocationRequester;

import com.shamanland.fab.FloatingActionButton;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends RoboFragment implements LocationRequester.LocationCallback {

    @InjectView(R.id.search_button) private FloatingActionButton searchButton;
    @InjectView(R.id.view_pager)    private ViewPager viewPager;

    private SwipeRouteDetailFragmentsAdapter swipeRouteDetailFragmentsAdapter;
    private ProgressDialog searchProgressDialog;
    private ProgressDialog locationUpdateProgressDialog;
    private SearchTask searchTask;
    private LocationRequester locationRequester;
    private Location currentLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRouteDetailFragmentsAdapter = new SwipeRouteDetailFragmentsAdapter();
        viewPager.setAdapter(swipeRouteDetailFragmentsAdapter);
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onRefreshSearch();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        locationRequester = new LocationRequester(getActivity().getApplicationContext());
        onRefreshSearch();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationUpdateProgressDialog != null && locationUpdateProgressDialog.isShowing()) {
            locationRequester.stopRequestingLocation();
            locationUpdateProgressDialog.dismiss();
        }
        if (searchProgressDialog != null && searchProgressDialog.isShowing()) {
            searchTask.cancel(true);
            searchProgressDialog.dismiss();
        }
    }

    private void onRefreshSearch() {
        locationUpdateProgressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.waiting_location_update), true, false);
        locationRequester.requestSingle(this);
    }

    private void onPreSearch() {
        searchProgressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.search_in_progress), true, false);
    }

    private void onRecommendationsAcquired(Recommendation[] recommendations) {
        if (recommendations != null) {
            swipeRouteDetailFragmentsAdapter.clearFragments();
            for (Recommendation recommendation : recommendations) {
                RouteDetailFragment fragment = RouteDetailFragment.newInstance(currentLocation, recommendation);
                swipeRouteDetailFragmentsAdapter.addFragment(fragment);
            }
            viewPager.setCurrentItem(0);
        }
    }

    private void onSearchFinally() {
        searchProgressDialog.dismiss();
    }

    @Override
    public void onLocationAcquired(android.location.Location location) {
        locationUpdateProgressDialog.dismiss();
        Location myLocation = new Location();
        myLocation.setLatitude(location.getLatitude());
        myLocation.setLongitude(location.getLongitude());
        currentLocation = myLocation;
        searchTask = new SearchTask(ExploreFragment.this);
        searchTask.transportation(RecommendationsApi.Transportation.DRIVING) // TODO: Hardcoded value
                .radius(50_000) // TODO: Hardcoded value
                .location(myLocation)
                .request();
    }

    @Override
    public void onLocationRequestTimeout() {
        locationUpdateProgressDialog.dismiss();
        Toast.makeText(getActivity().getApplicationContext(),
                R.string.location_update_timeout, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationProvidersDisabled() {
        locationUpdateProgressDialog.dismiss();
        Toast.makeText(getActivity().getApplicationContext(),
                R.string.enable_location_updates, Toast.LENGTH_SHORT).show();
    }

    private class SwipeRouteDetailFragmentsAdapter extends FragmentStatePagerAdapter {

        private List<RouteDetailFragment> fragments;

        private SwipeRouteDetailFragmentsAdapter() {
            super(getChildFragmentManager());
            fragments = new ArrayList<>();
        }

        private void clearFragments() {
            fragments.clear();
            notifyDataSetChanged();
        }

        private void addFragment(RouteDetailFragment fragment) {
            fragments.add(fragment);
            notifyDataSetChanged();
        }

        @Override
        public RouteDetailFragment getItem(int position) {
            if (!fragments.isEmpty() && position >= 0) return fragments.get(position);
            else return null;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (!fragments.isEmpty() && position >= 0) return getString(R.string.route_format, position + 1);
            else return null;
        }
    }

    private static class SearchTask extends RecommendationsByFriendsLikesAsyncTask {

        private WeakReference<ExploreFragment> reference;

        private SearchTask(ExploreFragment fragment) {
            super(fragment.getActivity().getApplicationContext());
            reference = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() throws Exception {
            ExploreFragment fragment = reference.get();
            if (fragment != null) fragment.onPreSearch();
        }

        @Override
        protected void onSuccess(Recommendation[] recommendations) throws Exception {
            ExploreFragment fragment = reference.get();
            if (fragment != null) fragment.onRecommendationsAcquired(recommendations);
        }

        @Override
        protected void onFinally() throws RuntimeException {
            ExploreFragment fragment = reference.get();
            if (fragment != null) fragment.onSearchFinally();
        }
    }
}