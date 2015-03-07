package com.grayfox.android.app.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.grayfox.android.app.R;
import com.grayfox.android.app.widget.LikeAdapter;
import com.grayfox.android.client.model.Category;
import com.grayfox.android.client.model.User;
import com.grayfox.android.client.task.GetUserLikesAsyncTask;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

import java.lang.ref.WeakReference;

public class FriendProfileFragment extends RoboFragment {

    private static final String FRIEND_ARG = "FRIEND";

    @InjectView(R.id.profile_image)  private CircleImageView profileImageView;
    @InjectView(R.id.user_name)      private TextView userNameTextView;
    @InjectView(R.id.progress_bar)   private ProgressBar progressBar;
    @InjectView(R.id.likes_header)   private LinearLayout likesHeaderLayout;
    @InjectView(R.id.likes_list)     private RecyclerView likesList;
    @InjectView(R.id.no_likes_found) private TextView noLikesFoundText;

    private GetUserLikesAsyncTask task;
    private User friend;

    public static FriendProfileFragment newInstance(User friend) {
        FriendProfileFragment fragment = new FriendProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(FRIEND_ARG, friend);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friend_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        likesList.setHasFixedSize(true);
        likesList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpUserArg();
        if (savedInstanceState == null) {
            task = new GetFriendLikesTask(this)
                    .foursquareId(friend.getFoursquareId());
            task.request();
        } else {
            if (task != null && task.isActive()) onPreExecuteFriendTask();
            else if (friend != null) {
                onGotFriendLikes(friend.getLikes());
                onFriendTaskFinally();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (task != null && task.isActive()) task.cancel(true);
    }

    private void setUpUserArg() {
        friend = getFriendArg();
        String userFullName = friend.getLastName() == null || friend.getLastName().trim().isEmpty() ? friend.getName() : new StringBuilder().append(friend.getName()).append(" ").append(friend.getLastName()).toString();
        userNameTextView.setText(userFullName);
        Picasso.with(getActivity())
                .load(friend.getPhotoUrl())
                .placeholder(R.drawable.ic_contact_picture)
                .into(profileImageView);
    }

    private void onPreExecuteFriendTask() {
        progressBar.setVisibility(View.VISIBLE);
        likesHeaderLayout.setVisibility(View.GONE);
        likesList.setVisibility(View.GONE);
        noLikesFoundText.setVisibility(View.GONE);
    }

    private void onGotFriendLikes(Category[] likes) {
        friend.setLikes(likes);
        if (likes == null || likes.length == 0) noLikesFoundText.setVisibility(View.VISIBLE);
        else {
            likesList.setAdapter(new LikeAdapter(likes));
            noLikesFoundText.setVisibility(View.GONE);
            likesHeaderLayout.setVisibility(View.VISIBLE);
            likesList.setVisibility(View.VISIBLE);
        }
    }

    private void onFriendTaskFinally() {
        progressBar.setVisibility(View.GONE);
    }

    private User getFriendArg() {
        return (User) getArguments().getSerializable(FRIEND_ARG);
    }

    private static class GetFriendLikesTask extends GetUserLikesAsyncTask {

        private WeakReference<FriendProfileFragment> reference;

        private GetFriendLikesTask(FriendProfileFragment fragment) {
            super(fragment.getActivity().getApplicationContext());
            reference = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() throws Exception {
            super.onPreExecute();
            FriendProfileFragment fragment = reference.get();
            if (fragment != null) fragment.onPreExecuteFriendTask();
        }

        @Override
        protected void onSuccess(Category[] likes) throws Exception {
            super.onSuccess(likes);
            FriendProfileFragment fragment = reference.get();
            if (fragment != null) fragment.onGotFriendLikes(likes);
        }

        @Override
        protected void onFinally() throws RuntimeException {
            super.onFinally();
            FriendProfileFragment fragment = reference.get();
            if (fragment != null) fragment.onFriendTaskFinally();
        }
    }
}