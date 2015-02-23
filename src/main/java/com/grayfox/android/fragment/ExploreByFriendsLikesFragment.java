package com.grayfox.android.fragment;

import android.location.Location;

import com.grayfox.android.client.RecommendationsApi;
import com.grayfox.android.client.model.Recommendation;
import com.grayfox.android.client.task.RecommendationsByFriendsLikesAsyncTask;

import java.lang.ref.WeakReference;

public class ExploreByFriendsLikesFragment extends BaseExploreFragment {

    private SearchTask searchTask;

    @Override
    protected void onChildRestoreInstanceState() {
        if (searchTask != null && searchTask.isActive()) onPreSearchRecommendations();
        else if (getLastRecommendations() != null) {
            onRecommendationsAcquired(getLastRecommendations());
            onSearchRecommendationsFinally();
        } else onPrepareForSearch();
    }

    @Override
    public void onLocationAcquired(Location location) {
        super.onLocationAcquired(location);
        searchTask = new SearchTask(this);
        searchTask.transportation(RecommendationsApi.Transportation.DRIVING) // TODO: Hardcoded value
                .radius(50_000) // TODO: Hardcoded value
                .location(getLastLocation())
                .request();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (searchTask != null) searchTask.cancel(true);
    }

    private static class SearchTask extends RecommendationsByFriendsLikesAsyncTask {

        private WeakReference<ExploreByFriendsLikesFragment> reference;

        private SearchTask(ExploreByFriendsLikesFragment fragment) {
            super(fragment.getActivity().getApplicationContext());
            reference = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() throws Exception {
            super.onPreExecute();
            ExploreByFriendsLikesFragment fragment = reference.get();
            if (fragment != null) fragment.onPreSearchRecommendations();
        }

        @Override
        protected void onSuccess(Recommendation[] recommendations) throws Exception {
            super.onSuccess(recommendations);
            ExploreByFriendsLikesFragment fragment = reference.get();
            if (fragment != null) fragment.onRecommendationsAcquired(recommendations);
        }

        @Override
        protected void onFinally() throws RuntimeException {
            super.onFinally();
            ExploreByFriendsLikesFragment fragment = reference.get();
            if (fragment != null) fragment.onSearchRecommendationsFinally();
        }
    }
}