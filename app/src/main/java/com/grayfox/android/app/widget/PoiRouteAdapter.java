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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.daimajia.swipe.util.Attributes;

import com.grayfox.android.app.R;
import com.grayfox.android.app.util.ColorTransformation;
import com.grayfox.android.client.model.Poi;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PoiRouteAdapter extends RecyclerSwipeAdapter<RecyclerView.ViewHolder> {

    private static final String TAG = "PoiRouteAdapter";

    private enum ViewType {TOP, MIDDLE, BOTTOM}

    private final List<Poi> poiSequence;
    private final Location currentLocation;
    private OnDeleteItemListener onDeleteItemListener;

    public PoiRouteAdapter(Location currentLocation) {
        this.currentLocation = currentLocation;
        poiSequence = new ArrayList<>();
        setMode(Attributes.Mode.Single);
    }

    public void add(Poi... pois) {
        poiSequence.addAll(Arrays.asList(pois));
    }

    public void set(Poi... pois) {
        poiSequence.clear();
        poiSequence.addAll(Arrays.asList(pois));
    }

    public void move(int from, int to) {
        int poiFromPosition = from-1;
        int poiToPosition = to-1;
        Poi removed = poiSequence.remove(poiFromPosition);
        poiSequence.add(poiToPosition, removed);
    }

    public void setOnDeleteItemListener(OnDeleteItemListener onDeleteItemListener) {
        this.onDeleteItemListener = onDeleteItemListener;
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Context context = holder.itemView.getContext();
        switch (ViewType.values()[getItemViewType(position)]) {
            case TOP:
                TopViewHolder topViewHolder = (TopViewHolder) holder;
                topViewHolder.markerImage.getDrawable().mutate().setColorFilter(context.getResources().getColor(R.color.secondary_text), PorterDuff.Mode.SRC_IN);
                topViewHolder.locationDescriptionTextView.setText(getAddress(context, currentLocation));
                break;
            case MIDDLE:
                final Poi poi = poiSequence.get(position-1);
                final MiddleViewHolder middleViewHolder = (MiddleViewHolder) holder;
                middleViewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
                middleViewHolder.swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
                Picasso.with(context)
                        .load(poi.getCategories()[0].getIconUrl())
                        .transform(new ColorTransformation(context.getResources().getColor(R.color.secondary_text)))
                        .placeholder(R.drawable.ic_generic_category)
                        .into(middleViewHolder.categoryImageView);
                middleViewHolder.categoryNameView.setText(poi.getCategories()[0].getName());
                middleViewHolder.poiNameView.setText(poi.getName());
                middleViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mItemManger.removeShownLayouts(middleViewHolder.swipeLayout);
                        poiSequence.remove(position-1);
                        notifyDataSetChanged();
                        mItemManger.closeAllItems();
                        if (onDeleteItemListener != null) onDeleteItemListener.onDelete(poi, position);
                    }
                });
                break;
            case BOTTOM:
                final Poi lastPoi = poiSequence.get(position-1);
                final BottomViewHolder bottomViewHolder = (BottomViewHolder) holder;
                bottomViewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
                bottomViewHolder.swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
                Picasso.with(context)
                        .load(lastPoi.getCategories()[0].getIconUrl())
                        .transform(new ColorTransformation(context.getResources().getColor(R.color.secondary_text)))
                        .placeholder(R.drawable.ic_generic_category)
                        .into(bottomViewHolder.categoryImageView);
                bottomViewHolder.categoryNameView.setText(lastPoi.getCategories()[0].getName());
                bottomViewHolder.poiNameView.setText(lastPoi.getName());
                bottomViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mItemManger.removeShownLayouts(bottomViewHolder.swipeLayout);
                        poiSequence.remove(position-1);
                        notifyDataSetChanged();
                        mItemManger.closeAllItems();
                        if (onDeleteItemListener != null) onDeleteItemListener.onDelete(lastPoi, position);
                    }
                });
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

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.swipe;
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

    public interface OnDeleteItemListener {
        void onDelete(Poi poi, int position);
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

        private SwipeLayout swipeLayout;
        private ImageView categoryImageView;
        private TextView poiNameView;
        private TextView categoryNameView;
        private ImageButton deleteButton;

        public MiddleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            categoryImageView = (ImageView) itemView.findViewById(R.id.category_image);
            poiNameView = (TextView) itemView.findViewById(R.id.poi_name);
            categoryNameView = (TextView) itemView.findViewById(R.id.category_name);
            deleteButton = (ImageButton) itemView.findViewById(R.id.delete_button);
        }
    }

    private static class BottomViewHolder extends RecyclerView.ViewHolder {

        private SwipeLayout swipeLayout;
        private ImageView categoryImageView;
        private TextView poiNameView;
        private TextView categoryNameView;
        private ImageButton deleteButton;

        public BottomViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            categoryImageView = (ImageView) itemView.findViewById(R.id.category_image);
            poiNameView = (TextView) itemView.findViewById(R.id.poi_name);
            categoryNameView = (TextView) itemView.findViewById(R.id.category_name);
            deleteButton = (ImageButton) itemView.findViewById(R.id.delete_button);
        }
    }
}