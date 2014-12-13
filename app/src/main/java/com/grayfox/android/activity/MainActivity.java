package com.grayfox.android.activity;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.grayfox.android.client.model.User;
import com.grayfox.android.client.task.GetSelfUserAsyncTask;
import com.grayfox.android.client.task.RecommendedSearchAsyncTask;

import com.grayfox.android.util.Images;
import com.shamanland.fab.FloatingActionButton;

import java.lang.ref.WeakReference;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActionBarActivity {

    @InjectView(R.id.search_button) private FloatingActionButton searchButton;
    @InjectView(R.id.drawer_layout) private DrawerLayout drawerLayout;
    @InjectView(R.id.user_picture)  private ImageView userPicture;
    @InjectView(R.id.user_name)     private TextView userNameText;

    private ActionBarDrawerToggle drawerToggle;
    private SearchTask searchTask;
    private ProgressDialog searchProgressDialog;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpDrawer();
        setUpMapIfNeeded();
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onSearch();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (searchProgressDialog != null && searchProgressDialog.isShowing()) {
            searchTask.cancel(true);
            searchProgressDialog.dismiss();
        }
    }

    private void setUpDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        new GetSelfUserTask(this).request();
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

    private void onGetSelfUserSuccess(User user) {
        userNameText.setText(new StringBuilder().append(user.getFirstName()).append(" ").append(user.getLastName()));
        new Images.ImageLoader(this)
                .setImageView(userPicture)
                .setLoadingResourceImageId(R.drawable.ic_contact_picture)
                .execute(user.getPhotoUrl());
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

    private static class GetSelfUserTask extends GetSelfUserAsyncTask {

        private WeakReference<MainActivity> reference;

        private GetSelfUserTask(MainActivity activity) {
            super(activity.getApplicationContext());
            reference = new WeakReference<>(activity);
        }

        @Override
        protected void onSuccess(User user) throws Exception {
            MainActivity activity = reference.get();
            if (activity != null) activity.onGetSelfUserSuccess(user);
        }
    }
}