package com.grayfox.android.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import com.grayfox.android.R;
import com.grayfox.android.client.BaseApiRequest;
import com.grayfox.android.client.RecommenderApiRequest;
import com.grayfox.android.client.model.Location;
import com.grayfox.android.client.model.Poi;
import com.grayfox.android.client.model.Recommendation;

import com.shamanland.fab.FloatingActionButton;

import javax.inject.Inject;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActionBarActivity {

    @InjectView(R.id.search_button) private FloatingActionButton searchButton;

    @Inject private RecommenderApiRequest recommenderApiRequest;

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
        recommenderApiRequest.radius(3000).category("shops")
                .transportation(RecommenderApiRequest.Transportation.DRIVING)
                .location(location)
                .asyncSearch(new BaseApiRequest.RequestCallback<Recommendation>() {

                    @Override
                    public void onSuccess(Recommendation recommendation) {
                        onRecommendationAcquired(recommendation);
                    }

                    @Override
                    public void onFailure(String reason) {
                        Toast.makeText(getApplicationContext(),
                                reason, Toast.LENGTH_SHORT).show();
                    }
                });
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
}