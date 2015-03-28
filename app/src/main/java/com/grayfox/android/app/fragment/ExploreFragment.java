package com.grayfox.android.app.fragment;

import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grayfox.android.app.R;
import com.grayfox.android.app.activity.MainActivity;
import com.grayfox.android.app.widget.CategoryCursorAdapter;
import com.grayfox.android.app.widget.PoiAdapter;
import com.grayfox.android.app.widget.RecommendationAdapter;
import com.grayfox.android.app.widget.util.PicassoMarker;
import com.grayfox.android.client.CategoriesApi;
import com.grayfox.android.client.PoisApi;
import com.grayfox.android.client.model.Category;
import com.grayfox.android.client.model.Poi;
import com.grayfox.android.client.model.Recommendation;
import com.grayfox.android.client.task.NetworkAsyncTask;
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
    @InjectView(R.id.progress_bar)       private ProgressBar progressBar;
    @InjectView(R.id.poi_list)           private RecyclerView poiList;

    @Inject private LocationManager locationManager;
    @Inject private InputMethodManager inputMethodManager;

    private boolean shouldRequestLocationUpdatesOnConnect;
    private boolean shouldRestoreCurrentLocationInMap;
    private boolean shouldRestoreRecommendationsInMap;
    private boolean shouldRestorePois;
    private MenuItem searchViewMenuItem;
    private SearchView searchView;
    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private Location currentLocation;
    private Recommendation[] recommendations;
    private Poi[] pois;
    private RecommendationAsyncTask recommendationsTask;
    private CategoryFilteringTask categoryFilteringTask;
    private PoisTask poisTask;
    private RecommendationAdapter recommendationAdapter;
    private PoiAdapter poiAdapter;
    private CategoryCursorAdapter categoryAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_explore, menu);
        categoryAdapter = new CategoryCursorAdapter(getActivity());
        searchViewMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchViewMenuItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_for_places));
        searchView.setSuggestionsAdapter(categoryAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && !newText.trim().isEmpty() && newText.length() > 1) {
                    categoryFilteringTask = new CategoryFilteringTask(ExploreFragment.this)
                            .query(newText);
                    categoryFilteringTask.request();
                }
                return false;
            }
        });
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) { return false; }

            @Override
            public boolean onSuggestionClick(int position) {
                onSuggestionClicked(position);
                return false;
            }
        });
    }

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
        poiAdapter = new PoiAdapter();
        poiAdapter.setOnClickListener(new PoiAdapter.OnClickListener() {
            @Override
            public void onClick(Poi poi) {
                onSelectPoi(poi);
            }
        });
        recommendationAdapter = new RecommendationAdapter();
        recommendationAdapter.setOnClickListener(new RecommendationAdapter.OnClickListener() {
            @Override
            public void onClick(Recommendation recommendation) {
                onSelectRecommendation(recommendation);
            }
        });
        poiList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        shouldRequestLocationUpdatesOnConnect = false;
        shouldRestoreCurrentLocationInMap = false;
        shouldRestoreRecommendationsInMap = false;
        shouldRestorePois = false;
        if (savedInstanceState == null) {
            shouldRequestLocationUpdatesOnConnect = true;
            shouldRestoreCurrentLocationInMap = false;
            shouldRestoreRecommendationsInMap = false;
        } else {
            if (currentLocation == null) {
                shouldRequestLocationUpdatesOnConnect = true;
            } else if (currentLocation != null && recommendations == null) {
                shouldRequestLocationUpdatesOnConnect = false;
                if (recommendationsTask == null || !recommendationsTask.isActive()) {
                    onLocationChanged(currentLocation);
                    shouldRestoreCurrentLocationInMap = true;
                } else if (recommendationsTask != null && recommendationsTask.isActive()) {
                    onPreExecuteRecommendationsTask();
                    shouldRestoreCurrentLocationInMap = true;
                }
            } else if (currentLocation != null && recommendations != null) {
                shouldRequestLocationUpdatesOnConnect = false;
                onRecommendationsAcquired(recommendations);
                onCompleteRecommendationsTask();
                shouldRestoreCurrentLocationInMap = true;
                shouldRestoreRecommendationsInMap = true;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopLocationUpdates();
        googleApiClient.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recommendationsTask != null && recommendationsTask.isActive()) recommendationsTask.cancel(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setPadding(0, 0, 0, (int) getResources().getDimension(R.dimen.recommendations_overlap));
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (shouldRestoreCurrentLocationInMap) showCurrentLocationInMap();
        if (shouldRestoreRecommendationsInMap) restoreRecommendationsInMap();
    }

    private void onSuggestionClicked(int position) {
        searchViewMenuItem.collapseActionView();
        searchView.setQuery(null, false);
        Category category = categoryAdapter.get(position);
        poisTask = new PoisTask(this)
                .currentLocation(getMapCenterLocation())
                .radius(getRadiusFromMapProjection())
                .categoryFoursquareId(category.getFoursquareId());
        poisTask.request();
    }

    private void showCurrentLocationInMap() {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(getString(R.string.current_location))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_location)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
    }

    private void restoreRecommendationsInMap() {
        for (Recommendation recommendation : recommendations) {
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(recommendation.getPoi().getLocation().getLatitude(), recommendation.getPoi().getLocation().getLongitude()))
                    .title(recommendation.getPoi().getName()));
            int backgroundResource = 0;
            switch (recommendation.getType()) {
                case SELF:
                    backgroundResource = R.drawable.ic_map_pin_pink;
                    break;
                case SOCIAL:
                    backgroundResource = R.drawable.ic_map_pin_dark_blue;
                    break;
            }
            Picasso.with(getActivity())
                    .load(recommendation.getPoi().getCategories()[0].getIconUrl())
                    .placeholder(R.drawable.ic_generic_category)
                    .into(new PicassoMarker(marker, backgroundResource, getActivity()));
        }
    }

    private void restorePoisInMap() {
        for (Poi poi : pois) {
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(poi.getLocation().getLatitude(), poi.getLocation().getLongitude()))
                    .title(poi.getName()));
            Picasso.with(getActivity())
                    .load(poi.getCategories()[0].getIconUrl())
                    .placeholder(R.drawable.ic_generic_category)
                    .into(new PicassoMarker(marker, R.drawable.ic_map_pin_light_blue, getActivity()));
        }
    }

    private void onPreLocationUpdate() {
        currentLocation = null;
        poiList.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
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
        shouldRequestLocationUpdatesOnConnect = false;
        currentLocation = location;
        stopLocationUpdates();
        onCompleteLocationUpdate();
        googleMap.clear();
        showCurrentLocationInMap();
        com.grayfox.android.client.model.Location myLocation = new com.grayfox.android.client.model.Location();
        myLocation.setLatitude(location.getLatitude());
        myLocation.setLongitude(location.getLongitude());
        recommendationsTask = new RecommendationsTask(this)
                .location(myLocation)
                .radius(getRadiusFromMapProjection());
        recommendationsTask.request();
    }

    private Location getMapCenterLocation() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(googleMap.getCameraPosition().target.latitude);
        location.setLongitude(googleMap.getCameraPosition().target.longitude);
        return location;
    }

    private int getRadiusFromMapProjection() {
        LatLng point1 = googleMap.getProjection().getVisibleRegion().nearLeft;
        LatLng point2 = googleMap.getProjection().getVisibleRegion().nearRight;
        float[] results = new float[1];
        Location.distanceBetween(point1.latitude, point1.longitude, point2.latitude, point2.longitude, results);
        return Math.round(results[0] / 2f);
    }

    private void stopLocationUpdates() {
        if (googleApiClient.isConnected()) LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    private void onCompleteLocationUpdate() {
        poiList.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void onPreExecuteRecommendationsTask() {
        poiList.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void onRecommendationsAcquired(Recommendation[] recommendations) {
        this.recommendations = recommendations;
        pois = null;
        poiList.setAdapter(recommendationAdapter);
        recommendationAdapter.clear();
        recommendationAdapter.add(recommendations);
        recommendationAdapter.notifyDataSetChanged();
        googleMap.clear();
        showCurrentLocationInMap();
        for (Recommendation recommendation : recommendations) {
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(recommendation.getPoi().getLocation().getLatitude(), recommendation.getPoi().getLocation().getLongitude()))
                    .title(recommendation.getPoi().getName()));
            int backgroundResource = 0;
            switch (recommendation.getType()) {
                case SELF:
                    backgroundResource = R.drawable.ic_map_pin_pink;
                    break;
                case SOCIAL:
                    backgroundResource = R.drawable.ic_map_pin_dark_blue;
                    break;
            }
            Picasso.with(getActivity())
                    .load(recommendation.getPoi().getCategories()[0].getIconUrl())
                    .placeholder(R.drawable.ic_generic_category)
                    .into(new PicassoMarker(marker, backgroundResource, getActivity()));
        }
    }

    private void onCompleteRecommendationsTask() {
        poiList.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void onAquireSuggestions(Category[] categories) {
        if (categories != null) categoryAdapter.add(categories);
    }

    private void onPreExecutePoisTask() {
        poiList.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void onPoisAcquired(Poi[] pois) {
        this.pois = pois;
        recommendations = null;
        poiList.setAdapter(poiAdapter);
        poiAdapter.clear();
        poiAdapter.add(pois);
        poiAdapter.notifyDataSetChanged();
        googleMap.clear();
        for (Poi poi : pois) {
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(poi.getLocation().getLatitude(), poi.getLocation().getLongitude()))
                    .title(poi.getName()));
            Picasso.with(getActivity())
                    .load(poi.getCategories()[0].getIconUrl())
                    .placeholder(R.drawable.ic_generic_category)
                    .into(new PicassoMarker(marker, R.drawable.ic_map_pin_light_blue, getActivity()));
        }
    }

    private void onCompletePoisTaskTask() {
        poiList.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void onSelectRecommendation(Recommendation recommendation) {
        RecommendedRouteFragment fragment = RecommendedRouteFragment.newInstance(currentLocation, recommendation.getPoi());
        ((MainActivity) getActivity()).setupFragment(fragment, true);
    }

    private void onSelectPoi(Poi poi) {
        RecommendedRouteFragment fragment = RecommendedRouteFragment.newInstance(currentLocation, poi);
        ((MainActivity) getActivity()).setupFragment(fragment, true);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Location services connected.");
        if (shouldRequestLocationUpdatesOnConnect) onRequestLocationUpdates();
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

    private static class CategoryFilteringTask extends NetworkAsyncTask<Category[]> {

        @Inject private CategoriesApi categoriesApi;

        private WeakReference<ExploreFragment> reference;
        private String query;

        private CategoryFilteringTask(ExploreFragment fragment) {
            super(fragment.getActivity().getApplicationContext());
            reference = new WeakReference<>(fragment);
        }

        public CategoryFilteringTask query(String query) {
            this.query = query;
            return this;
        }

        @Override
        public Category[] call() throws Exception {
            return categoriesApi.awaitCategoriesLikeName(query);
        }

        @Override
        protected void onSuccess(Category[] categories) throws Exception {
            super.onSuccess(categories);
            ExploreFragment fragment = reference.get();
            if (fragment != null) fragment.onAquireSuggestions(categories);
        }
    }
    private static class PoisTask extends NetworkAsyncTask<Poi[]> {

        @Inject private PoisApi poisApi;

        private WeakReference<ExploreFragment> reference;
        private String categoryFoursquareId;
        private Location currentLocation;
        private int radius;

        private PoisTask(ExploreFragment fragment) {
            super(fragment.getActivity().getApplicationContext());
            reference = new WeakReference<>(fragment);
        }

        public PoisTask currentLocation(Location currentLocation) {
            this.currentLocation = currentLocation;
            return this;
        }

        public PoisTask radius(int radius) {
            this.radius = radius;
            return this;
        }

        public PoisTask categoryFoursquareId(String categoryFoursquareId) {
            this.categoryFoursquareId = categoryFoursquareId;
            return this;
        }

        @Override
        protected void onPreExecute() throws Exception {
            super.onPreExecute();
            ExploreFragment fragment = reference.get();
            if (fragment != null) fragment.onPreExecutePoisTask();
        }

        @Override
        public Poi[] call() throws Exception {
            com.grayfox.android.client.model.Location myLocation = new com.grayfox.android.client.model.Location();
            myLocation.setLatitude(currentLocation.getLatitude());
            myLocation.setLongitude(currentLocation.getLongitude());
            return poisApi.awaitSearchByCategory(myLocation, radius, categoryFoursquareId);
        }

        @Override
        protected void onSuccess(Poi[] pois) throws Exception {
            super.onSuccess(pois);
            ExploreFragment fragment = reference.get();
            if (fragment != null) fragment.onPoisAcquired(pois);
        }

        @Override
        protected void onFinally() throws RuntimeException {
            super.onFinally();
            ExploreFragment fragment = reference.get();
            if (fragment != null) fragment.onCompletePoisTaskTask();
        }
    }
}