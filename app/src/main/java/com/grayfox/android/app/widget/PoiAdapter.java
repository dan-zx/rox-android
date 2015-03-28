package com.grayfox.android.app.widget;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grayfox.android.app.R;
import com.grayfox.android.client.model.Poi;
import com.grayfox.android.client.model.Recommendation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PoiAdapter extends RecyclerView.Adapter<PoiAdapter.ViewHolder> {

    private final List<Poi> pois;

    private OnClickListener onClickListener;

    public PoiAdapter() {
        pois = new ArrayList<>();
    }

    public void add(Poi... pois) {
        this.pois.addAll(Arrays.asList(pois));
    }

    public void clear() {
        pois.clear();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.poi_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Poi poi = pois.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) onClickListener.onClick(poi);
            }
        });
        holder.categoryImageView.setBackgroundResource(R.drawable.ic_map_pin_light_blue);
        Picasso.with(holder.itemView.getContext())
                .load(poi.getCategories()[0].getIconUrl())
                .placeholder(R.drawable.ic_generic_category)
                .into(holder.categoryImageView);
        holder.poiNameTextView.setText(poi.getName());
    }

    @Override
    public int getItemCount() {
        return pois.size();
    }

    public static interface OnClickListener {
        void onClick(Poi poi);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView categoryImageView;
        private TextView poiNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            categoryImageView = (ImageView) itemView.findViewById(R.id.category_image);
            poiNameTextView = (TextView) itemView.findViewById(R.id.poi_name);
        }
    }
}