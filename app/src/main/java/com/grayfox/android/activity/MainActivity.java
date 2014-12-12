package com.grayfox.android.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import com.grayfox.android.R;
import com.grayfox.android.client.RecommenderApi;
import com.grayfox.android.client.model.Location;
import com.grayfox.android.client.model.Poi;
import com.grayfox.android.client.model.Recommendation;
import com.grayfox.android.client.task.RecommendedSearchAsyncTask;

import com.shamanland.fab.FloatingActionButton;

import java.lang.ref.WeakReference;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActionBarActivity {

    @InjectView(R.id.search_button) private FloatingActionButton searchButton;

    private SearchTask searchTask;
    private ProgressDialog searchProgressDialog;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpMapIfNeeded();
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onSearch();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (searchProgressDialog != null && searchProgressDialog.isShowing()) {
            searchTask.cancel(true);
            searchProgressDialog.dismiss();
        }
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment)).getMap();
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        }
    }

    private void onSearch() {
        Location location = new Location();
        location.setLatitude(map.getCameraPosition().target.latitude);
        location.setLongitude(map.getCameraPosition().target.longitude);
        searchTask = new SearchTask(this);
        searchTask.radius(3000).category("shops")
                .transportation(RecommenderApi.Transportation.DRIVING)
                .location(location)
                .request();
    }

    private void onPreSearch() {
        searchProgressDialog = ProgressDialog.show(this, null, getString(R.string.search_in_progress), true, false);
    }

    private void onRecommendationAcquired(Recommendation recommendation) {
        map.clear();
        Log.d("TAG", recommendation.toString());
        if (!recommendation.getPois().isEmpty()) {
            for (Poi poi : recommendation.getPois()) {
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(poi.getLocation().getLatitude(), poi.getLocation().getLongitude()))
                        .title(poi.getName()));
            }
            map.animateCamera(CameraUpdateFactory.newLatLng(
                    new LatLng(recommendation.getPois().get(0).getLocation().getLatitude(), recommendation.getPois().get(0).getLocation().getLongitude())));
        }

        if (!recommendation.getRoutePoints().isEmpty()) {
            PolylineOptions pathOptions = new PolylineOptions().color(Color.RED);
            for (Location point : recommendation.getRoutePoints()) {
                pathOptions.add(new LatLng(point.getLatitude(), point.getLongitude()));
            }
            map.addPolyline(pathOptions);
        }
    }

    private void onSearchFinally() {
        searchProgressDialog.dismiss();
    }

    private static class SearchTask extends RecommendedSearchAsyncTask {

        private WeakReference<MainActivity> reference;

        private SearchTask(MainActivity activity) {
            super(activity.getApplicationContext());
            reference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() throws Exception {
            MainActivity activity = reference.get();
            if (activity != null) activity.onPreSearch();
        }

        @Override
        protected void onSuccess(Recommendation recommendation) throws Exception {
            MainActivity activity = reference.get();
            if (activity != null) activity.onRecommendationAcquired(recommendation);
        }

        @Override
        protected void onFinally() throws RuntimeException {
            MainActivity activity = reference.get();
            if (activity != null) activity.onSearchFinally();
        }
    }
}