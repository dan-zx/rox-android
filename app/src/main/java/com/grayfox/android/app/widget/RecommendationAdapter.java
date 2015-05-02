package com.grayfox.android.app.widget;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.grayfox.android.app.R;
import com.grayfox.android.client.model.Recommendation;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {

    private final List<Recommendation> recommendations;

    private OnClickListener onClickListener;
    private OnBuildRouteButtonClickListener onBuildRouteButtonClickListener;

    public RecommendationAdapter() {
        recommendations = new ArrayList<>();
    }

    public void add(Recommendation... recommendations) {
        this.recommendations.addAll(Arrays.asList(recommendations));
    }

    public void clear() {
        recommendations.clear();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnBuildRouteButtonClickListener(OnBuildRouteButtonClickListener onBuildRouteButtonClickListener) {
        this.onBuildRouteButtonClickListener = onBuildRouteButtonClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recommendation_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Recommendation recommendation = recommendations.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) onClickListener.onClick(recommendation);
            }
        });
        holder.buildRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onBuildRouteButtonClickListener != null) onBuildRouteButtonClickListener.onClick(recommendation);
            }
        });
        switch (recommendation.getType()) {
            case GLOBAL:
                holder.categoryImageView.setBackgroundResource(R.drawable.ic_map_pin_light_blue);
                break;
            case SELF:
                holder.categoryImageView.setBackgroundResource(R.drawable.ic_map_pin_pink);
                break;
            case SOCIAL:
                holder.categoryImageView.setBackgroundResource(R.drawable.ic_map_pin_dark_blue);
                break;
        }
        Picasso.with(holder.itemView.getContext())
                .load(recommendation.getPoi().getCategories()[0].getIconUrl())
                .placeholder(R.drawable.ic_generic_category)
                .into(holder.categoryImageView);
        holder.poiNameTextView.setText(recommendation.getPoi().getName());
        if (recommendation.getPoi().getFoursquareRating() != null) holder.poiRatingTextView.setText(String.valueOf(recommendation.getPoi().getFoursquareRating()));
        holder.reasonTextView.setText(recommendation.getReason());
    }

    @Override
    public int getItemCount() {
        return recommendations.size();
    }

    public interface OnClickListener {
        void onClick(Recommendation recommendation);
    }

    public interface OnBuildRouteButtonClickListener {
        void onClick(Recommendation recommendation);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView categoryImageView;
        private TextView poiNameTextView;
        private TextView poiRatingTextView;
        private TextView reasonTextView;
        private Button buildRouteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            categoryImageView = (ImageView) itemView.findViewById(R.id.category_image);
            poiNameTextView = (TextView) itemView.findViewById(R.id.poi_name);
            poiRatingTextView = (TextView) itemView.findViewById(R.id.poi_rating);
            reasonTextView = (TextView) itemView.findViewById(R.id.reason);
            buildRouteButton = (Button) itemView.findViewById(R.id.build_route);
        }
    }
}