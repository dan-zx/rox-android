package com.grayfox.android.app.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

public class LocationRequester {

    private static final long TIMEOUT = 60_000;

    private boolean isStopped;
    private LocationManager locationManager;
    private SingleLocationRequestListener locationListener;
    private LocationCallback callback;

    public LocationRequester(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new SingleLocationRequestListener();
        isStopped = true;
    }

    public void requestSingle(final LocationCallback callback) {
        this.callback = callback;
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isStopped = false;
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isStopped) {
                        stopRequestingLocation();
                        callback.onLocationRequestTimeout();
                    }
                }
            }, TIMEOUT);
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            isStopped = false;
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isStopped) {
                        stopRequestingLocation();
                        callback.onLocationRequestTimeout();
                    }
                }
            }, TIMEOUT);
        } else {
            isStopped = true;
            callback.onLocationProvidersDisabled();
        }
    }

    public void stopRequestingLocation() {
        isStopped = true;
        locationManager.removeUpdates(locationListener);
    }

    public boolean isStopped() {
        return isStopped;
    }

    private class SingleLocationRequestListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            stopRequestingLocation();
            callback.onLocationAcquired(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }

        @Override
        public void onProviderEnabled(String s) { }

        @Override
        public void onProviderDisabled(String s) { }
    }

    public static interface LocationCallback {

        void onLocationAcquired(Location location);
        void onLocationRequestTimeout();
        void onLocationProvidersDisabled();
    }
}