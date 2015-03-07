package com.grayfox.android.app.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationGeocoder {

    private static final String TAG = LocationGeocoder.class.getSimpleName();

    public static String getAddress(Context context, Location location) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses == null || addresses.isEmpty()) return null;
            Address address = addresses.get(0);
            StringBuilder addressBuilder = new StringBuilder();
            String prefix = "";
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressBuilder.append(prefix).append(address.getAddressLine(i));
                prefix = "/";
            }
            return addressBuilder.toString();
        } catch (IOException ex) {
            Log.e(TAG, "Service unavailable", ex);
            return null;
        } catch (Exception ex) {
            Log.e(TAG, "Error while retrieving address", ex);
            return null;
        }
    }
}