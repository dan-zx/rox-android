package com.grayfox.android.app.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grayfox.android.app.R;
import com.grayfox.android.app.widget.util.ColorTransformation;
import com.grayfox.android.client.model.Category;

import com.squareup.picasso.Picasso;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ViewHolder> {

    private final Category[] likes;

    public LikeAdapter(Category[] likes) {
        this.likes = likes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.like_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        holder.likeNameTextView.setText(likes[position].getName());
        Picasso.with(context)
                .load(likes[position].getIconUrl())
                .transform(new ColorTransformation(context.getResources().getColor(R.color.secondary_text)))
                .placeholder(R.drawable.ic_generic_category)
                .into(holder.likeImageView);
    }

    @Override
    public int getItemCount() {
        return likes.length;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView likeImageView;
        private TextView likeNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            likeImageView = (ImageView) itemView.findViewById(R.id.like_image);
            likeNameTextView = (TextView) itemView.findViewById(R.id.like_name);
        }
    }
}