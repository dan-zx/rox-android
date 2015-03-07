package com.grayfox.android.app.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grayfox.android.app.R;
import com.grayfox.android.client.model.User;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private OnItemClickListener listener;
    private final User[] friends;

    public FriendAdapter(User[] friends) {
        this.friends = friends;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Context context = holder.itemView.getContext();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) listener.onClick(position);
            }
        });
        String userFullName = friends[position].getLastName() == null || friends[position].getLastName().trim().isEmpty() ? friends[position].getName() : new StringBuilder().append(friends[position].getName()).append(" ").append(friends[position].getLastName()).toString();
        holder.userNameTextView.setText(userFullName);
        Picasso.with(context)
                .load(friends[position].getPhotoUrl())
                .placeholder(R.drawable.ic_contact_picture)
                .into(holder.profileImageView);
    }

    @Override
    public int getItemCount() {
        return friends.length;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView profileImageView;
        private TextView userNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            profileImageView = (CircleImageView) itemView.findViewById(R.id.profile_image);
            userNameTextView = (TextView) itemView.findViewById(R.id.user_name);
        }
    }

    public static interface OnItemClickListener {
        void onClick(int position);
    }
}