package com.grayfox.android.app.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import com.grayfox.android.app.R;
import com.grayfox.android.app.util.ColorTransformation;
import com.grayfox.android.client.model.Category;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LikeDeletableAdapter extends RecyclerSwipeAdapter<LikeDeletableAdapter.ViewHolder> {

    private final List<Category> categories;

    private OnRemoveLikeListener removeLikeListener;

    public LikeDeletableAdapter(Category... categories) {
        this.categories = new ArrayList<>(Arrays.asList(categories));
    }

    public boolean add(Category category) {
        if (!categories.contains(category)) return categories.add(category);
        return false;
    }

    public void setOnRemoveLikeListener(OnRemoveLikeListener removeLikeListener) {
        this.removeLikeListener = removeLikeListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.like_item_deletable, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Context context = holder.itemView.getContext();
        holder.likeNameTextView.setText(categories.get(position).getName());
        Picasso.with(context)
                .load(categories.get(position).getIconUrl())
                .transform(new ColorTransformation(context.getResources().getColor(R.color.secondary_text)))
                .placeholder(R.drawable.ic_generic_category)
                .into(holder.likeImageView);
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        holder.swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemManger.removeShownLayouts(holder.swipeLayout);
                Category removedCategory = categories.remove(position);
                notifyDataSetChanged();
                mItemManger.closeAllItems();
                if (removeLikeListener != null)  removeLikeListener.onRemove(removedCategory);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.swipe;
    }

    public static interface OnRemoveLikeListener {
        void onRemove(Category like);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private SwipeLayout swipeLayout;
        private ImageView likeImageView;
        private TextView likeNameTextView;
        private ImageButton deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            likeImageView = (ImageView) itemView.findViewById(R.id.like_image);
            likeNameTextView = (TextView) itemView.findViewById(R.id.like_name);
            deleteButton = (ImageButton) itemView.findViewById(R.id.delete_button);
        }
    }
}