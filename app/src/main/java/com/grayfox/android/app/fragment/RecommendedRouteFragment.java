package com.grayfox.android.app.fragment;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.getbase.floatingactionbutton.CustomFloatingActionsMenu;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.grayfox.android.app.R;
import com.grayfox.android.app.dao.AccessTokenDao;
import com.grayfox.android.app.widget.PoiRouteAdapter;
import com.grayfox.android.app.widget.util.PicassoMarker;
import com.grayfox.android.client.RecommendationsApi;
import com.grayfox.android.client.model.Poi;
import com.grayfox.android.client.task.NetworkAsyncTask;
import com.squareup.picasso.Picasso;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class RecommendedRouteFragment extends RoboFragment implements OnMapReadyCallback {

    private static final String MAP_FRAGMENT_TAG = "MAP_FRAGMENT";
    private static final String CURRENT_LOCATION_ARG = "CURRENT_LOCATION";
    private static final String SEED_ARG = "SEED";

    @InjectView(R.id.recalculating_route_progress_bar) private ProgressBar recalculatingRouteProgressBar;
    @InjectView(R.id.walking_directions_button)        private FloatingActionButton walkingDirectionsButton;
    @InjectView(R.id.transit_directions_button)        private FloatingActionButton transitDirectionsButton;
    @InjectView(R.id.driving_directions_button)        private FloatingActionButton drivingDirectionsButton;
    @InjectView(R.id.bike_directions_button)           private FloatingActionButton bikeDirectionsButton;
    @InjectView(R.id.building_route_layout)            private LinearLayout buildingRouteLayout;
    @InjectView(R.id.directions_menu)                  private CustomFloatingActionsMenu directionsMenu;
    @InjectView(R.id.route_list)                       private RecyclerView routeList;
    @InjectView(R.id.card_view)                        private CardView cardView;

    private boolean shouldRestoreRoute;
    private GoogleMap googleMap;
    private RecalculateRouteTask recalculateRouteTask;
    private RouteBuilderTask routeBuilderTask;
    private PoiRouteAdapter poiRouteAdapter;
    private DirectionsRoute route;
    private Poi[] nextPois;

    public static RecommendedRouteFragment newInstance(Location currentLocation, Poi seed) {
        RecommendedRouteFragment fragment = new RecommendedRouteFragment();
        Bundle args = new Bundle();
        args.putParcelable(CURRENT_LOCATION_ARG, currentLocation);
        args.putSerializable(SEED_ARG, seed);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recommended_route, container, false);
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
        walkingDirectionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                directionsMenu.collapse();
                if (routeBuilderTask != null && !routeBuilderTask.isActive()) recalculateRoute(TravelMode.WALKING);
            }
        });
        transitDirectionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                directionsMenu.collapse();
                if (routeBuilderTask != null && !routeBuilderTask.isActive()) recalculateRoute(TravelMode.TRANSIT);
            }
        });
        drivingDirectionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                directionsMenu.collapse();
                if (routeBuilderTask != null && !routeBuilderTask.isActive()) recalculateRoute(TravelMode.DRIVING);
            }
        });
        bikeDirectionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                directionsMenu.collapse();
                if (routeBuilderTask != null && !routeBuilderTask.isActive()) recalculateRoute(TravelMode.BICYCLING);
            }
        });
        cardView.getLayoutParams().height += (int) getResources().getDimension(R.dimen.list_overlap);
        routeList.setLayoutManager(new LinearLayoutManager(getActivity()));
        poiRouteAdapter = new PoiRouteAdapter(getCurrentLocationArg());
        poiRouteAdapter.add(getSeedArg());
        routeList.setAdapter(poiRouteAdapter);
        fragment.getMapAsync(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        shouldRestoreRoute = false;
        if (savedInstanceState == null) {
            routeBuilderTask = new RouteBuilderTask(this)
                    .seed(getSeedArg())
                    .origin(getCurrentLocationArg())
                    .travelMode(getCurrentTravelMode());
            routeBuilderTask.request();
        } else {
            if (routeBuilderTask != null && routeBuilderTask.isActive()) onPreExecuteRouteBuilderTask();
            if (recalculateRouteTask != null && recalculateRouteTask.isActive()) onPreExcecuteRecalculateRouteTask();
            else shouldRestoreRoute = true;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        addPoiMarker(getSeedArg());
        currentLocationInMap();
        if (shouldRestoreRoute) {
            if (nextPois != null) onAcquireNextPois(nextPois);
            if (route != null) onAcquireRoute(route);
            onCompleteRouteBuilderTask();
        }
    }

    private void currentLocationInMap() {
        Location currentLocation = getCurrentLocationArg();
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(getString(R.string.current_location))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
    }

    private void onPreExecuteRouteBuilderTask() {
        buildingRouteLayout.setVisibility(View.VISIBLE);
        routeList.setVisibility(View.GONE);
    }

    private void onAcquireNextPois(Poi[] nextPois) {
        this.nextPois = nextPois;
        if (nextPois != null && nextPois.length > 0) {
            poiRouteAdapter.add(nextPois);
            poiRouteAdapter.notifyDataSetChanged();
            for (Poi poi : nextPois) addPoiMarker(poi);
        }
    }

    private void onAcquireRoute(DirectionsRoute route) {
        this.route = route;
        if(route != null) {
            List<com.google.maps.model.LatLng> polyline = route.overviewPolyline.decodePath();
            PolylineOptions pathOptions = new PolylineOptions().color(Color.RED);
            for (com.google.maps.model.LatLng point : polyline) pathOptions.add(new LatLng(point.lat, point.lng));
            googleMap.addPolyline(pathOptions);
        } else {
            Toast.makeText(getActivity().getApplicationContext(),
                    R.string.unavailable_route, Toast.LENGTH_SHORT).show();
        }
    }

    private void onCompleteRouteBuilderTask() {
        buildingRouteLayout.setVisibility(View.GONE);
        routeList.setVisibility(View.VISIBLE);
    }

    private void onPreExcecuteRecalculateRouteTask() {
        googleMap.clear();
        currentLocationInMap();
        addPoiMarker(getSeedArg());
        for (Poi poi : nextPois) addPoiMarker(poi);
        recalculatingRouteProgressBar.setVisibility(View.VISIBLE);
    }

    private void recalculateRoute(TravelMode travelMode) {
        saveCurrentTravelMode(travelMode);
        recalculateRouteTask = new RecalculateRouteTask(this)
                .seed(getSeedArg())
                .origin(getCurrentLocationArg())
                .nextPois(nextPois)
                .travelMode(travelMode);
        recalculateRouteTask.request();
    }

    private void onCompleteRecalculateRoute() {
        recalculatingRouteProgressBar.setVisibility(View.GONE);
    }

    private void addPoiMarker(Poi poi) {
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(poi.getLocation().getLatitude(), poi.getLocation().getLongitude()))
                .title(poi.getName()));
        Picasso.with(getActivity())
                .load(poi.getCategories()[0].getIconUrl())
                .placeholder(R.drawable.ic_generic_category)
                .into(new PicassoMarker(marker, getActivity()));
    }

    private Location getCurrentLocationArg() {
        return (Location) getArguments().getParcelable(CURRENT_LOCATION_ARG);
    }

    private Poi getSeedArg() {
        return (Poi) getArguments().getSerializable(SEED_ARG);
    }

    private TravelMode getCurrentTravelMode() {
        String travelModeStr = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getString(getString(R.string.travel_mode_key), TravelMode.DRIVING.name());
        return TravelMode.valueOf(travelModeStr);
    }

    private void saveCurrentTravelMode(TravelMode selectedTravelMode) {
        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .edit()
                .putString(getString(R.string.travel_mode_key), selectedTravelMode.name())
                .commit();
    }

    private static class RouteBuilderTask extends NetworkAsyncTask<Object[]> {

        @Inject private GeoApiContext geoApiContext;
        @Inject private AccessTokenDao accessTokenDao;
        @Inject private RecommendationsApi recommendationsApi;

        private WeakReference<RecommendedRouteFragment> reference;
        private Poi seed;
        private Location origin;
        private TravelMode travelMode;

        private RouteBuilderTask(RecommendedRouteFragment fragment) {
            super(fragment.getActivity().getApplicationContext());
            reference = new WeakReference<>(fragment);
        }

        public RouteBuilderTask seed(Poi seed) {
            this.seed = seed;
            return this;
        }

        private RouteBuilderTask origin(Location origin) {
            this.origin = origin;
            return this;
        }

        private RouteBuilderTask travelMode(TravelMode travelMode) {
            this.travelMode = travelMode;
            return this;
        }

        @Override
        protected void onPreExecute() throws Exception {
            super.onPreExecute();
            RecommendedRouteFragment fragment = reference.get();
            if (fragment != null) fragment.onPreExecuteRouteBuilderTask();
        }

        @Override
        public Object[] call() throws Exception {
            Poi[] nextPois = recommendationsApi.awaitNextPois(accessTokenDao.fetchAccessToken(), seed);
            List<Poi> pois = new ArrayList<>();
            pois.add(seed);
            pois.addAll(Arrays.asList(nextPois));
            String[] waypoints = new String[pois.size()-1];
            for (int i = 0; i < waypoints.length; i++) waypoints[i] = toGoogleMapsServicesLatLng(pois.get(i).getLocation());
            DirectionsRoute[] routes = DirectionsApi.newRequest(geoApiContext)
                    .origin(toGoogleMapsServicesLatLng(origin))
                    .destination(toGoogleMapsServicesLatLng(pois.get(pois.size()-1).getLocation()))
                    .mode(travelMode)
                    .waypoints(waypoints)
                    .await();
            return new Object[] {nextPois, routes == null || routes.length == 0 ? null : routes[0]};
        }

        @Override
        protected void onSuccess(Object[] objects) throws Exception {
            super.onSuccess(objects);
            onAcquiredNextPois((Poi[]) objects[0]);
            onAcquiredRoute((DirectionsRoute) objects[1]);
        }

        private void onAcquiredNextPois(Poi[] nextPois) {
            RecommendedRouteFragment fragment = reference.get();
            if (fragment != null) fragment.onAcquireNextPois(nextPois);
        }

        private void onAcquiredRoute(DirectionsRoute route) {
            RecommendedRouteFragment fragment = reference.get();
            if (fragment != null) fragment.onAcquireRoute(route);
        }

        @Override
        protected void onFinally() throws RuntimeException {
            super.onFinally();
            RecommendedRouteFragment fragment = reference.get();
            if (fragment != null) fragment.onCompleteRouteBuilderTask();
        }

        private String toGoogleMapsServicesLatLng(Location location) {
            return new StringBuilder().append(location.getLatitude())
                    .append(',').append(location.getLongitude()).toString();
        }

        private String toGoogleMapsServicesLatLng(com.grayfox.android.client.model.Location location) {
            return new StringBuilder().append(location.getLatitude())
                    .append(',').append(location.getLongitude()).toString();
        }
    }

    private static class RecalculateRouteTask extends NetworkAsyncTask<DirectionsRoute> {

        @Inject private GeoApiContext geoApiContext;

        private WeakReference<RecommendedRouteFragment> reference;
        private Poi seed;
        private Poi[] nextPois;
        private Location origin;
        private TravelMode travelMode;

        private RecalculateRouteTask(RecommendedRouteFragment fragment) {
            super(fragment.getActivity().getApplicationContext());
            reference = new WeakReference<>(fragment);
        }

        public RecalculateRouteTask seed(Poi seed) {
            this.seed = seed;
            return this;
        }

        public RecalculateRouteTask nextPois(Poi[] nextPois) {
            this.nextPois = nextPois;
            return this;
        }

        private RecalculateRouteTask origin(Location origin) {
            this.origin = origin;
            return this;
        }

        private RecalculateRouteTask travelMode(TravelMode travelMode) {
            this.travelMode = travelMode;
            return this;
        }

        @Override
        protected void onPreExecute() throws Exception {
            super.onPreExecute();
            RecommendedRouteFragment fragment = reference.get();
            if (fragment != null) fragment.onPreExcecuteRecalculateRouteTask();
        }

        @Override
        public DirectionsRoute call() throws Exception {
            List<Poi> pois = new ArrayList<>();
            pois.add(seed);
            pois.addAll(Arrays.asList(nextPois));
            String[] waypoints = new String[pois.size()-1];
            for (int i = 0; i < waypoints.length; i++) waypoints[i] = toGoogleMapsServicesLatLng(pois.get(i).getLocation());
            DirectionsRoute[] routes = DirectionsApi.newRequest(geoApiContext)
                    .origin(toGoogleMapsServicesLatLng(origin))
                    .destination(toGoogleMapsServicesLatLng(pois.get(pois.size()-1).getLocation()))
                    .mode(travelMode)
                    .waypoints(waypoints)
                    .await();
            return routes == null || routes.length == 0 ? null : routes[0];
        }

        @Override
        protected void onSuccess(DirectionsRoute route) throws Exception {
            super.onSuccess(route);
            RecommendedRouteFragment fragment = reference.get();
            if (fragment != null) fragment.onAcquireRoute(route);
        }

        @Override
        protected void onFinally() throws RuntimeException {
            super.onFinally();
            RecommendedRouteFragment fragment = reference.get();
            if (fragment != null) fragment.onCompleteRecalculateRoute();
        }

        private String toGoogleMapsServicesLatLng(Location location) {
            return new StringBuilder().append(location.getLatitude())
                    .append(',').append(location.getLongitude()).toString();
        }

        private String toGoogleMapsServicesLatLng(com.grayfox.android.client.model.Location location) {
            return new StringBuilder().append(location.getLatitude())
                    .append(',').append(location.getLongitude()).toString();
        }
    }
}