package com.grayfox.android.app.fragment;

import android.content.Context;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grayfox.android.app.R;
import com.grayfox.android.app.widget.RecommendationAdapter;
import com.grayfox.android.client.model.Recommendation;
import com.grayfox.android.client.task.RecommendationAsyncTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

public class ExploreFragment extends RoboFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int GOOGLE_API_CLIENT_CONNECTION_FAILURE_RESOLUTION_REQUEST = 5;
    private static final String TAG = ExploreFragment.class.getSimpleName();

    @InjectView(R.id.my_location_button) private FloatingActionButton myLocationButton;
    @InjectView(R.id.searching_layout)   private LinearLayout searchingLayout;
    @InjectView(R.id.searching_text)     private TextView searchingTextView;
    @InjectView(R.id.card_view)          private CardView cardView;
    @InjectView(R.id.poi_list)           private RecyclerView poiList;

    @Inject private LocationManager locationManager;

    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private Location currentLocation;
    private RecommendationAsyncTask recommendationsTask;
    private RecommendationAdapter recommendationAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recommendationAdapter = new RecommendationAdapter();
        cardView.getLayoutParams().height += (int) getResources().getDimension(R.dimen.list_overlap);
        poiList.setLayoutManager(new LinearLayoutManager(getActivity()));
        poiList.setAdapter(recommendationAdapter);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRequestLocationUpdates();
            }
        });
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        ((SupportMapFragment)(getChildFragmentManager().findFragmentById(R.id.map))).getMapAsync(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            stopLocationUpdates();
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    private void onPreLocationUpdate() {
        poiList.setVisibility(View.GONE);
        searchingLayout.setVisibility(View.VISIBLE);
        searchingTextView.setText(R.string.waiting_location_update);
    }

    private boolean areAnyLocationProvidersEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void onRequestLocationUpdates() {
        if (areAnyLocationProvidersEnabled()) {
            onPreLocationUpdate();
            LocationRequest locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1_000)        // 10 seconds
                    .setFastestInterval(1 * 1_000); // 1 second
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            Toast.makeText(getActivity().getApplicationContext(),
                    R.string.enable_location_updates,
                    Toast.LENGTH_LONG).show();
            onCompleteLocationUpdate();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        stopLocationUpdates();
        onCompleteLocationUpdate();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(getString(R.string.current_location))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
        int radius = getRediusFromMapProjection();
        googleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeColor(Color.BLUE));
        com.grayfox.android.client.model.Location myLocation = new com.grayfox.android.client.model.Location();
        myLocation.setLatitude(location.getLatitude());
        myLocation.setLongitude(location.getLongitude());
        recommendationsTask = new RecommendationsTask(this)
                .location(myLocation)
                .radius(radius);
        recommendationsTask.request();
    }

    private int getRediusFromMapProjection() {
        LatLng point1 = googleMap.getProjection().getVisibleRegion().nearLeft;
        LatLng point2 = googleMap.getProjection().getVisibleRegion().nearRight;
        double dLat = Math.toRadians(point2.latitude - point1.latitude);
        double dLon = Math.toRadians(point2.longitude - point1.longitude);
        double lat1 = Math.toRadians(point1.latitude);
        double lat2 = Math.toRadians(point2.latitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return (int) Math.round(6378137 * c);
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    private void onCompleteLocationUpdate() {
        poiList.setVisibility(View.GONE);
        searchingLayout.setVisibility(View.GONE);
    }

    private void onPreExecuteRecommendationsTask() {
        poiList.setVisibility(View.GONE);
        searchingLayout.setVisibility(View.VISIBLE);
        searchingTextView.setText(R.string.searching_recommendations);
    }

    private void onRecommendationsAcquired(Recommendation[] recommendations) {
        recommendationAdapter.clear();
        recommendationAdapter.add(recommendations);
        recommendationAdapter.notifyDataSetChanged();
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                .title(getString(R.string.current_location))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        for (Recommendation recommendation : recommendations) {
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(recommendation.getPoi().getLocation().getLatitude(), recommendation.getPoi().getLocation().getLongitude()))
                    .title(recommendation.getPoi().getName()));
            Picasso.with(getActivity())
                    .load(recommendation.getPoi().getCategories()[0].getIconUrl())
                    .placeholder(R.drawable.ic_generic_category)
                    .into(new PicassoMarker(marker, getActivity()));
        }
    }

    private void onCompleteRecommendationsTask() {
        poiList.setVisibility(View.VISIBLE);
        searchingLayout.setVisibility(View.GONE);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Location services connected.");
        onRequestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        switch (cause) {
            case CAUSE_SERVICE_DISCONNECTED:
                Log.d(TAG, "Location services disconnected. Please reconnect.");
                break;
            case CAUSE_NETWORK_LOST:
                Log.d(TAG, "Location services has lost connection. Please reconnect.");
                break;
            default: Log.d(TAG, "Location services suspended. Please reconnect.");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(), GOOGLE_API_CLIENT_CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException ex) {
                Log.e(TAG, "Intent sender exception", ex);
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    private static class RecommendationsTask extends RecommendationAsyncTask {

        private WeakReference<ExploreFragment> reference;

        private RecommendationsTask(ExploreFragment fragment) {
            super(fragment.getActivity().getApplicationContext());
            reference = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() throws Exception {
            super.onPreExecute();
            ExploreFragment fragment = reference.get();
            if (fragment != null) fragment.onPreExecuteRecommendationsTask();
        }

        @Override
        protected void onSuccess(Recommendation[] recommendations) throws Exception {
            super.onSuccess(recommendations);
            ExploreFragment fragment = reference.get();
            if (fragment != null) fragment.onRecommendationsAcquired(recommendations);
        }

        @Override
        protected void onFinally() throws RuntimeException {
            super.onFinally();
            ExploreFragment fragment = reference.get();
            if (fragment != null) fragment.onCompleteRecommendationsTask();
        }
    }

    private static class PicassoMarker implements Target {

        private Marker marker;
        private View layout;

        private PicassoMarker(Marker marker, Context context) {
            this.marker = marker;
            layout = LayoutInflater.from(context).inflate(R.layout.category_marker, null);
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