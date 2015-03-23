package com.grayfox.android.app.fragment;

import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.grayfox.android.app.activity.MainActivity;
import com.grayfox.android.app.widget.RecommendationAdapter;
import com.grayfox.android.app.widget.util.PicassoMarker;
import com.grayfox.android.client.model.Recommendation;
import com.grayfox.android.client.task.RecommendationAsyncTask;
import com.squareup.picasso.Picasso;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

public class ExploreFragment extends RoboFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int GOOGLE_API_CLIENT_CONNECTION_FAILURE_RESOLUTION_REQUEST = 5;
    private static final String MAP_FRAGMENT_TAG = "MAP_FRAGMENT";
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
        FragmentManager fragmentManager = getChildFragmentManager();
        SupportMapFragment fragment = (SupportMapFragment) fragmentManager.findFragmentByTag(MAP_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.map_container, fragment, MAP_FRAGMENT_TAG)
                    .commit();
        }
        recommendationAdapter = new RecommendationAdapter();
        recommendationAdapter.setOnClickListener(new RecommendationAdapter.OnClickListener() {
            @Override
            public void onClick(Recommendation recommendation) {
                onSelectRecommendation(recommendation);
            }
        });
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
        fragment.getMapAsync(this);
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
        int radius = getRadiusFromMapProjection();
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

    private int getRadiusFromMapProjection() {
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

    private void onSelectRecommendation(Recommendation recommendation) {
        RecommendedRouteFragment fragment = RecommendedRouteFragment.newInstance(currentLocation, recommendation.getPoi());
        ((MainActivity) getActivity()).setupFragment(fragment, true);
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
}