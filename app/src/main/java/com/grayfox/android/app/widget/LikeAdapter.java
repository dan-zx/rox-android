package com.grayfox.android.app.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.grayfox.android.app.R;
import com.grayfox.android.app.util.ColorTransformation;
import com.grayfox.android.client.model.Category;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ViewHolder> {

    private final List<Category> categories;

    private OnRemoveLikeListener removeLikeListener;
    private boolean isEditable;

    public LikeAdapter(Category... categories) {
        this.categories = new ArrayList<>(Arrays.asList(categories));
    }

    public boolean add(Category category) {
        if (!categories.contains(category)) return categories.add(category);
        return false;
    }

    public void setOnRemoveLikeListener(OnRemoveLikeListener removeLikeListener) {
        this.removeLikeListener = removeLikeListener;
    }

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.like_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Context context = holder.itemView.getContext();
        holder.likeNameTextView.setText(categories.get(position).getName());
        Picasso.with(context)
                .load(categories.get(position).getIconUrl())
                .transform(new ColorTransformation(context.getResources().getColor(R.color.secondary_text)))
                .placeholder(R.drawable.ic_generic_category)
                .into(holder.likeImageView);
        if (isEditable) {
            holder.removeLikeButton.setVisibility(View.VISIBLE);
            holder.removeLikeButton.getDrawable().mutate().setColorFilter(context.getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
            holder.removeLikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (removeLikeListener != null) {
                        removeLikeListener.onRemove(categories.get(position));
                        categories.remove(position);
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static interface OnRemoveLikeListener {
        void onRemove(Category like);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView likeImageView;
        private TextView likeNameTextView;
        private ImageButton removeLikeButton;

        public ViewHolder(View itemView) {
            super(itemView);
            likeImageView = (ImageView) itemView.findViewById(R.id.like_image);
            likeNameTextView = (TextView) itemView.findViewById(R.id.like_name);
            removeLikeButton = (ImageButton) itemView.findViewById(R.id.remove_like_button);
        }
    }
}