package com.grayfox.android.app.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grayfox.android.app.R;
import com.grayfox.android.app.widget.util.ColorTransformation;
import com.grayfox.android.client.model.Poi;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PoiRouteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = PoiRouteAdapter.class.getSimpleName();

    private static enum ViewType {TOP, MIDDLE, BOTTOM}

    private final List<Poi> poiSequence;
    private final Location currentLocation;

    public PoiRouteAdapter(Location currentLocation) {
        this.currentLocation = currentLocation;
        poiSequence = new ArrayList<>();
    }

    public void add(Poi... pois) {
        poiSequence.addAll(Arrays.asList(pois));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (ViewType.values()[viewType]) {
            case TOP:
                View topView = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_top_item, parent, false);
                return new TopViewHolder(topView);
            case MIDDLE:
                View middleView = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_middle_item, parent, false);
                return new MiddleViewHolder(middleView);
            case BOTTOM:
                View bottomView = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_bottom_item, parent, false);
                return new BottomViewHolder(bottomView);
            default: return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        switch (ViewType.values()[getItemViewType(position)]) {
            case TOP:
                TopViewHolder topViewHolder = (TopViewHolder) holder;
                topViewHolder.markerImage.getDrawable().mutate().setColorFilter(context.getResources().getColor(R.color.secondary_text), PorterDuff.Mode.SRC_IN);
                topViewHolder.locationDescriptionTextView.setText(getAddress(context, currentLocation));
                break;
            case MIDDLE:
                Poi poi = poiSequence.get(position-1);
                MiddleViewHolder middleViewHolder = (MiddleViewHolder) holder;
                Picasso.with(context)
                        .load(poi.getCategories()[0].getIconUrl())
                        .transform(new ColorTransformation(context.getResources().getColor(R.color.secondary_text)))
                        .placeholder(R.drawable.ic_generic_category)
                        .into(middleViewHolder.categoryImageView);
                middleViewHolder.categoryNameView.setText(poi.getCategories()[0].getName());
                middleViewHolder.poiNameView.setText(poi.getName());
                break;
            case BOTTOM:
                Poi lastPoi = poiSequence.get(position-1);
                BottomViewHolder bottomViewHolder = (BottomViewHolder) holder;
                Picasso.with(context)
                        .load(lastPoi.getCategories()[0].getIconUrl())
                        .transform(new ColorTransformation(context.getResources().getColor(R.color.secondary_text)))
                        .placeholder(R.drawable.ic_generic_category)
                        .into(bottomViewHolder.categoryImageView);
                bottomViewHolder.categoryNameView.setText(lastPoi.getCategories()[0].getName());
                bottomViewHolder.poiNameView.setText(lastPoi.getName());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return ViewType.TOP.ordinal();
        if (position == getItemCount()-1) return ViewType.BOTTOM.ordinal();
        return ViewType.MIDDLE.ordinal();
    }

    @Override
    public int getItemCount() {
        return poiSequence.size()+1;
    }

    private String getAddress(Context context, Location location) {
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

    private static class TopViewHolder extends RecyclerView.ViewHolder {

        private ImageView markerImage;
        private TextView locationDescriptionTextView;

        public TopViewHolder(View itemView) {
            super(itemView);
            markerImage = (ImageView) itemView.findViewById(R.id.marker_image);
            locationDescriptionTextView = (TextView) itemView.findViewById(R.id.location_description);
        }
    }

    private static class MiddleViewHolder extends RecyclerView.ViewHolder {

        private ImageView categoryImageView;
        private TextView poiNameView;
        private TextView categoryNameView;

        public MiddleViewHolder(View itemView) {
            super(itemView);
            categoryImageView = (ImageView) itemView.findViewById(R.id.category_image);
            poiNameView = (TextView) itemView.findViewById(R.id.poi_name);
            categoryNameView = (TextView) itemView.findViewById(R.id.category_name);
        }
    }

    private static class BottomViewHolder extends RecyclerView.ViewHolder {

        private ImageView categoryImageView;
        private TextView poiNameView;
        private TextView categoryNameView;

        public BottomViewHolder(View itemView) {
            super(itemView);
            categoryImageView = (ImageView) itemView.findViewById(R.id.category_image);
            poiNameView = (TextView) itemView.findViewById(R.id.poi_name);
            categoryNameView = (TextView) itemView.findViewById(R.id.category_name);
        }
    }
}
