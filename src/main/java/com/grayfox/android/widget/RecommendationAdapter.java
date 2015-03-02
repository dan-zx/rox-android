package com.grayfox.android.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grayfox.android.R;
import com.grayfox.android.client.model.Recommendation;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class RecommendationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static enum ViewType {TOP, MIDDLE, BOTTOM}

    private final String originAddress;
    private final Recommendation recommendation;

    public RecommendationAdapter(String originAddress, Recommendation recommendation) {
        this.originAddress = originAddress;
        this.recommendation = recommendation;
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
                topViewHolder.locationDescriptionTextView.setText(originAddress);
                break;
            case MIDDLE:
                MiddleViewHolder middleViewHolder = (MiddleViewHolder) holder;
                Picasso.with(context)
                        .load(recommendation.getPoiSequence()[position-1].getCategories()[0].getIconUrl())
                        .transform(new ColorTransformation(context, R.color.secondary_text))
                        .placeholder(R.drawable.ic_generic_category)
                        .into(middleViewHolder.categoryImageView);
                middleViewHolder.categoryNameView.setText(recommendation.getPoiSequence()[position-1].getCategories()[0].getName());
                middleViewHolder.poiNameView.setText(recommendation.getPoiSequence()[position-1].getName());
                break;
            case BOTTOM:
                BottomViewHolder bottomViewHolder = (BottomViewHolder) holder;
                Picasso.with(context)
                        .load(recommendation.getPoiSequence()[position-1].getCategories()[0].getIconUrl())
                        .transform(new ColorTransformation(context, R.color.secondary_text))
                        .placeholder(R.drawable.ic_generic_category)
                        .into(bottomViewHolder.categoryImageView);
                bottomViewHolder.categoryNameView.setText(recommendation.getPoiSequence()[position-1].getCategories()[0].getName());
                bottomViewHolder.poiNameView.setText(recommendation.getPoiSequence()[position-1].getName());
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
        return recommendation.getPoiSequence().length+1;
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

    private static class ColorTransformation implements Transformation {

        private final Context context;
        private final int colorId;

        private ColorTransformation(Context context, int colorId) {
            this.context = context;
            this.colorId = colorId;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            int [] allpixels = new int [source.getHeight() * source.getWidth()];
            source.getPixels(allpixels, 0, source.getWidth(), 0, 0, source.getWidth(), source.getHeight());
            for(int i = 0; i < allpixels.length; i++) {
                if( allpixels[i] != Color.TRANSPARENT) allpixels[i] = context.getResources().getColor(colorId);
            }
            Bitmap result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            result.setPixels(allpixels, 0, source.getWidth(), 0, 0, source.getWidth(), source.getHeight());
            source.recycle();
            return result;
        }

        @Override
        public String key() {
            return "toColor()";
        }
    }
}