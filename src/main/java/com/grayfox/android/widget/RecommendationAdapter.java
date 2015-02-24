package com.grayfox.android.widget;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grayfox.android.R;
import com.grayfox.android.client.model.Location;
import com.grayfox.android.client.model.Recommendation;
import com.grayfox.android.location.LocationGeocoder;

import com.squareup.picasso.Picasso;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {

    private final Location origin;
    private final Recommendation recommendation;

    public RecommendationAdapter(Location origin, Recommendation recommendation) {
        this.origin = origin;
        this.recommendation = recommendation;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_row, parent, false);
        ViewHolder holder = new ViewHolder(rootView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0) {
            holder.categoryImageView.setImageResource(R.drawable.ic_generic_category);
            holder.categoryNameView.setText(LocationGeocoder.getAddress(holder.poiNameView.getContext(), origin));
            holder.poiNameView.setText(R.string.your_location);
        } else {
            Picasso.with(holder.categoryImageView.getContext())
                    .load(recommendation.getPoiSequence()[position-1].getCategories()[0].getIconUrl())
                    .placeholder(R.drawable.ic_generic_category)
                    .into(holder.categoryImageView);
            holder.categoryNameView.setText(recommendation.getPoiSequence()[position-1].getCategories()[0].getName());
            holder.poiNameView.setText(recommendation.getPoiSequence()[position-1].getName());
        }
    }

    @Override
    public int getItemCount() {
        return recommendation.getPoiSequence().length+1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView categoryImageView;
        private TextView poiNameView;
        private TextView categoryNameView;

        private ViewHolder(View rootView) {
            super(rootView);
            categoryImageView = (ImageView) rootView.findViewById(R.id.category_image);
            poiNameView = (TextView) rootView.findViewById(R.id.poi_name);
            categoryNameView = (TextView) rootView.findViewById(R.id.category_name);
        }
    }
}