package com.grayfox.android.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grayfox.android.R;
import com.grayfox.android.client.RecommendationsApi;
import com.grayfox.android.client.model.Location;
import com.grayfox.android.client.model.Recommendation;
import com.grayfox.android.client.task.RecommendationsByFriendsLikesAsyncTask;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ExploreFragment2 extends RoboFragment {

    @InjectView(R.id.pager_strip) private PagerTabStrip pagerStrip;
    @InjectView(R.id.view_pager)  private ViewPager viewPager;

    private SwipeRouteDetailFragmentsAdapter swipeRouteDetailFragmentsAdapter;
    private ProgressDialog searchProgressDialog;
    private SearchTask searchTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore2, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRouteDetailFragmentsAdapter = new SwipeRouteDetailFragmentsAdapter(getChildFragmentManager());
        viewPager.setAdapter(swipeRouteDetailFragmentsAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onSearch();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (searchProgressDialog != null && searchProgressDialog.isShowing()) {
            searchTask.cancel(true);
            searchProgressDialog.dismiss();
        }
    }

    private void onSearch() {
        Location location = new Location();
        location.setLatitude(18.989961);
        location.setLongitude(-98.206079);
        searchTask = new SearchTask(this);
        searchTask.transportation(RecommendationsApi.Transportation.DRIVING)
                .radius(50_000)
                .location(location)
                .request();
    }

    private void onPreSearch() {
        searchProgressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.search_in_progress), true, false);
    }

    private void onRecommendationsAcquired(Recommendation[] recommendations) {
        if (recommendations != null) for (Recommendation recommendation : recommendations) {
            RouteDetailFragment fragment = RouteDetailFragment.newInstance(recommendation);
            swipeRouteDetailFragmentsAdapter.addFragment(fragment);
        }
    }

    private void onSearchFinally() {
        searchProgressDialog.dismiss();
    }

    private static class SwipeRouteDetailFragmentsAdapter extends FragmentStatePagerAdapter {

        private List<RouteDetailFragment> fragments;

        private SwipeRouteDetailFragmentsAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            fragments = new ArrayList<>();
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
            if (!fragments.isEmpty() && position >= 0) return "Ruta " + position;
            else return null;
        }
    }

    private static class SearchTask extends RecommendationsByFriendsLikesAsyncTask {

        private WeakReference<ExploreFragment2> reference;

        private SearchTask(ExploreFragment2 fragment) {
            super(fragment.getActivity().getApplicationContext());
            reference = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() throws Exception {
            ExploreFragment2 fragment = reference.get();
            if (fragment != null) fragment.onPreSearch();
        }

        @Override
        protected void onSuccess(Recommendation[] recommendations) throws Exception {
            ExploreFragment2 fragment = reference.get();
            if (fragment != null) fragment.onRecommendationsAcquired(recommendations);
        }

        @Override
        protected void onFinally() throws RuntimeException {
            ExploreFragment2 fragment = reference.get();
            if (fragment != null) fragment.onSearchFinally();
        }
    }
}
