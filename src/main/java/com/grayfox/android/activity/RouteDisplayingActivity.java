package com.grayfox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import com.grayfox.android.R;
import com.grayfox.android.client.model.Location;
import com.grayfox.android.client.model.Poi;
import com.grayfox.android.client.model.Recommendation;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_route_displaying)
public class RouteDisplayingActivity extends RoboActionBarActivity implements OnMapReadyCallback {

    private static final String RECOMMENDATION_ARG = "RECOMMENDATION";
    private static final String ORIGIN_LOCATION_ARG = "ORIGIN_LOCATION";

    private Location origin;
    private Recommendation recommendation;

    public static Intent getIntent(Context context, Location origin, Recommendation recommendation) {
        Intent intent = new Intent(context, RouteDisplayingActivity.class);
        intent.putExtra(ORIGIN_LOCATION_ARG, origin);
        intent.putExtra(RECOMMENDATION_ARG, recommendation);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        origin = (Location) getIntent().getExtras().getSerializable(ORIGIN_LOCATION_ARG);
        recommendation = (Recommendation) getIntent().getExtras().getSerializable(RECOMMENDATION_ARG);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (recommendation.getPoiSequence().length > 0) {
            for (Poi poi : recommendation.getPoiSequence()) {
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(poi.getLocation().getLatitude(), poi.getLocation().getLongitude()))
                        .title(poi.getName()));
                Picasso.with(this)
                        .load(poi.getCategories()[0].getIconUrl())
                        .placeholder(R.drawable.ic_generic_category)
                        .into(new PicassoMarker(marker, this));
            }
        }
        if (recommendation.getRoutePoints().length > 0) {
            PolylineOptions pathOptions = new PolylineOptions().color(Color.RED);
            for (int index = 0; index < recommendation.getRoutePoints().length; index++) {
                pathOptions.add(new LatLng(recommendation.getRoutePoints()[index].getLatitude(), recommendation.getRoutePoints()[index].getLongitude()));
            }
            googleMap.addPolyline(pathOptions);
        }
        LatLng latLngOrigin = new LatLng(origin.getLatitude(), origin.getLongitude());
        googleMap.addMarker(new MarkerOptions()
                .position(latLngOrigin)
                .title(getString(R.string.your_location))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOrigin, 13f));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private static class PicassoMarker implements Target {

        private Marker marker;
        private View layout;

        private PicassoMarker(Marker marker, Context context) {
            this.marker = marker;
            layout = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.category_marker, null);
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconToLayoutBitmap(bitmap)));
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconToLayoutBitmap(errorDrawable)));
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconToLayoutBitmap(placeHolderDrawable)));
        }

        private Bitmap iconToLayoutBitmap(Drawable iconDrawable) {
            ImageView categoryImageView = (ImageView) layout.findViewById(R.id.category_image);
            categoryImageView.setImageDrawable(iconDrawable);
            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());
            Bitmap bitmap = Bitmap.createBitmap(layout.getMeasuredWidth(), layout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);
            layout.draw(c);
            return bitmap;
        }

        private Bitmap iconToLayoutBitmap(Bitmap iconBitmap) {
            ImageView categoryImageView = (ImageView) layout.findViewById(R.id.category_image);
            categoryImageView.setImageBitmap(iconBitmap);
            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());
            Bitmap bitmap = Bitmap.createBitmap(layout.getMeasuredWidth(), layout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);
            layout.draw(c);
            return bitmap;
        }
    }
}