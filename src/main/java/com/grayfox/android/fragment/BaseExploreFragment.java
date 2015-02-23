package com.grayfox.android.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import com.getbase.floatingactionbutton.FloatingActionButton;

import com.grayfox.android.R;
import com.grayfox.android.client.model.Location;
import com.grayfox.android.client.model.Recommendation;
import com.grayfox.android.location.LocationRequester;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseExploreFragment extends RoboFragment implements LocationRequester.LocationCallback {

    @InjectView(R.id.searching_layout) private LinearLayout searchingLayout;
    @InjectView(R.id.searching_text)   private TextView searchingTextView;
    @InjectView(R.id.search_button)    private FloatingActionButton searchButton;
    @InjectView(R.id.pager_strip)      private PagerSlidingTabStrip pagerStrip;
    @InjectView(R.id.view_pager)       private ViewPager viewPager;

    private SwipeRouteDetailFragmentsAdapter swipeRouteDetailFragmentsAdapter;
    private LocationRequester locationRequester;
    private Location currentLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager.setVisibility(View.GONE);
        pagerStrip.setVisibility(View.GONE);
        searchButton.setVisibility(View.VISIBLE);
        searchingLayout.setVisibility(View.GONE);
        swipeRouteDetailFragmentsAdapter = new SwipeRouteDetailFragmentsAdapter();
        viewPager.setAdapter(swipeRouteDetailFragmentsAdapter);
        pagerStrip.setViewPager(viewPager);
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onLocateUser();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        locationRequester = new LocationRequester(getActivity().getApplicationContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        locationRequester.stopRequestingLocation();
    }

    private void onLocateUser() {
        viewPager.setVisibility(View.GONE);
        pagerStrip.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);
        searchingLayout.setVisibility(View.VISIBLE);
        searchingTextView.setText(R.string.waiting_location_update);
        locationRequester.requestSingle(this);
    }

    @Override
    public void onLocationAcquired(android.location.Location location) {
        Location myLocation = new Location();
        myLocation.setLatitude(location.getLatitude());
        myLocation.setLongitude(location.getLongitude());
        currentLocation = myLocation;
    }

    @Override
    public void onLocationRequestTimeout() {
        viewPager.setVisibility(View.GONE);
        pagerStrip.setVisibility(View.GONE);
        searchButton.setVisibility(View.VISIBLE);
        searchingLayout.setVisibility(View.GONE);
        Toast.makeText(getActivity().getApplicationContext(),
                R.string.location_update_timeout, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationProvidersDisabled() {
        viewPager.setVisibility(View.GONE);
        pagerStrip.setVisibility(View.GONE);
        searchButton.setVisibility(View.VISIBLE);
        searchingLayout.setVisibility(View.GONE);
        Toast.makeText(getActivity().getApplicationContext(),
                R.string.enable_location_updates, Toast.LENGTH_SHORT).show();
    }

    protected void onPreSearchRecommendations() {
        viewPager.setVisibility(View.GONE);
        pagerStrip.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);
        searchingLayout.setVisibility(View.VISIBLE);
        searchingTextView.setText(R.string.search_in_progress);
    }

    protected void onRecommendationsAcquired(Recommendation[] recommendations) {
        viewPager.setVisibility(View.VISIBLE);
        pagerStrip.setVisibility(View.VISIBLE);
        if (recommendations != null) {
            swipeRouteDetailFragmentsAdapter.clearFragments();
            for (Recommendation recommendation : recommendations) {
                RouteDetailFragment fragment = RouteDetailFragment.newInstance(currentLocation, recommendation);
                swipeRouteDetailFragmentsAdapter.addFragment(fragment);
            }
            viewPager.setCurrentItem(0);
        }
    }

    protected void onSearchRecommendationsFinally() {
        searchingLayout.setVisibility(View.GONE);
        searchButton.setVisibility(View.VISIBLE);
    }

    protected Location getCurrentLocation() {
        return currentLocation;
    }

    private class SwipeRouteDetailFragmentsAdapter extends FragmentPagerAdapter {

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
            if (!fragments.isEmpty() && position >= 0)
                return getString(R.string.route_format, position + 1);
            else return null;
        }
    }
}