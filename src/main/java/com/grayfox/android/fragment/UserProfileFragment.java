package com.grayfox.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import com.getbase.floatingactionbutton.FloatingActionButton;

import com.grayfox.android.R;
import com.grayfox.android.activity.FriendProfileActivity;
import com.grayfox.android.client.model.Category;
import com.grayfox.android.client.model.User;
import com.grayfox.android.client.task.CompleteUserAsyncTask;
import com.grayfox.android.widget.FriendAdapter;
import com.grayfox.android.widget.LikeAdapter;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

import java.lang.ref.WeakReference;

public class UserProfileFragment extends RoboFragment {

    private static final String USER_ARG = "USER";

    @InjectView(R.id.profile_image) private CircleImageView profileImageView;
    @InjectView(R.id.user_name)     private TextView userNameTextView;
    @InjectView(R.id.edit_button)   private FloatingActionButton editButton;
    @InjectView(R.id.pager_strip)   private PagerSlidingTabStrip pagerStrip;
    @InjectView(R.id.view_pager)    private ViewPager viewPager;
    @InjectView(R.id.progress_bar)  private ProgressBar progressBar;

    private CompleteUserTask task;
    private User user;

    public static UserProfileFragment newInstance(User user) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(USER_ARG, user);
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
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpUserArg();
        if (savedInstanceState == null) {
            task = new CompleteUserTask(this);
            task.currentUser(user);
            task.request();
        } else {
            if (task != null && task.isActive()) onPreExecuteTask();
            else if (user != null) {
                onCompleteUser(user);
                onTaskFinally();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (task != null && task.isActive()) task.cancel(true);
    }

    private void setUpUserArg() {
        user = getUserArg();
        String userFullName = user.getLastName() == null || user.getLastName().trim().isEmpty() ? user.getName() : new StringBuilder().append(user.getName()).append(" ").append(user.getLastName()).toString();
        userNameTextView.setText(userFullName);
        Picasso.with(getActivity())
                .load(user.getPhotoUrl())
                .placeholder(R.drawable.ic_contact_picture)
                .into(profileImageView);
    }

    private void onPreExecuteTask() {
        pagerStrip.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void onCompleteUser(User user) {
        this.user = user;
        pagerStrip.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        viewPager.setAdapter(new SwipeFragmentsAdapter()
                .setFriendsFragment(FriendsFragment.newInstance(user.getFriends()))
                .setLikesFragment(LikesFragment.newInstance(user.getLikes())));
        pagerStrip.setViewPager(viewPager);
    }

    private void onTaskFinally() {
        progressBar.setVisibility(View.GONE);
    }

    private User getUserArg() {
        return (User) getArguments().getSerializable(USER_ARG);
    }

    private static class CompleteUserTask extends CompleteUserAsyncTask {

        private WeakReference<UserProfileFragment> reference;

        private CompleteUserTask(UserProfileFragment fragment) {
            super(fragment.getActivity().getApplicationContext());
            reference = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() throws Exception {
            super.onPreExecute();
            UserProfileFragment fragment = reference.get();
            if (fragment != null) fragment.onPreExecuteTask();
        }

        @Override
        protected void onSuccess(User user) throws Exception {
            super.onSuccess(user);
            UserProfileFragment fragment = reference.get();
            if (fragment != null) fragment.onCompleteUser(user);
        }

        @Override
        protected void onFinally() throws RuntimeException {
            super.onFinally();
            UserProfileFragment fragment = reference.get();
            if (fragment != null) fragment.onTaskFinally();
        }
    }

    private class SwipeFragmentsAdapter extends FragmentPagerAdapter {

        private FriendsFragment friendsFragment;
        private LikesFragment likesFragment;

        private SwipeFragmentsAdapter() {
            super(getChildFragmentManager());
        }

        private SwipeFragmentsAdapter setFriendsFragment(FriendsFragment friendsFragment) {
            this.friendsFragment = friendsFragment;
            return this;
        }

        private SwipeFragmentsAdapter setLikesFragment(LikesFragment likesFragment) {
            this.likesFragment = likesFragment;
            return this;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return friendsFragment;
                case 1: return likesFragment;
                default: return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return getString(R.string.profile_user_friends_tab);
                case 1: return getString(R.string.profile_user_likes_tab);
                default: return null;
            }
        }
    }

    public static class FriendsFragment extends RecyclerFragment {

        private static final String FRIENDS_LENGTH_ARG = "FRIENDS_LENGTH";
        private static final String FRIENDS_FORMAT_ARG = "FRIEND_%d";

        private static FriendsFragment newInstance(User[] friends) {
            FriendsFragment fragment = new FriendsFragment();
            Bundle args = new Bundle();
            args.putInt(FRIENDS_LENGTH_ARG, friends.length);
            for (int i = 0; i < friends.length; i++) args.putSerializable(String.format(FRIENDS_FORMAT_ARG, i), friends[i]);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getRecyclerView().setHasFixedSize(true);
            getRecyclerView().setLayoutManager(new LinearLayoutManager(getActivity()));
            final User[] friends = getFriendsArg();
            if (friends.length == 0) {
                getNoItemsTextView().setText(R.string.no_friends_found);
                getRecyclerView().setVisibility(View.GONE);
            } else {
                getNoItemsTextView().setVisibility(View.GONE);
                FriendAdapter adapter = new FriendAdapter(friends);
                adapter.setOnItemClickListener(new FriendAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(int position) {
                        startActivity(FriendProfileActivity.getIntent(getActivity(), friends[position]));
                    }
                });
                getRecyclerView().setAdapter(adapter);
            }
        }

        private User[] getFriendsArg() {
            User[] friends = new User[getArguments().getInt(FRIENDS_LENGTH_ARG)];
            for (int i = 0; i < friends.length; i++) friends[i] = (User) getArguments().getSerializable(String.format(FRIENDS_FORMAT_ARG, i));
            return friends;
        }
    }

    public static class LikesFragment extends RecyclerFragment {

        private static final String LIKES_LENGTH_ARG = "LIKES_LENGTH";
        private static final String LIKES_FORMAT_ARG = "LIKE_%d";

        private static LikesFragment newInstance(Category[] likes) {
            LikesFragment fragment = new LikesFragment();
            Bundle args = new Bundle();
            args.putInt(LIKES_LENGTH_ARG, likes.length);
            for (int i = 0; i < likes.length; i++) args.putSerializable(String.format(LIKES_FORMAT_ARG, i), likes[i]);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getRecyclerView().setHasFixedSize(true);
            getRecyclerView().setLayoutManager(new LinearLayoutManager(getActivity()));
            Category[] likes = getLikesArg();
            if (likes.length == 0) {
                getNoItemsTextView().setText(R.string.no_likes_found);
                getRecyclerView().setVisibility(View.GONE);
            } else {
                getNoItemsTextView().setVisibility(View.GONE);
                getRecyclerView().setAdapter(new LikeAdapter(likes));
            }
        }

        private Category[] getLikesArg() {
            Category[] likes = new Category[getArguments().getInt(LIKES_LENGTH_ARG)];
            for (int i = 0; i < likes.length; i++) likes[i] = (Category) getArguments().getSerializable(String.format(LIKES_FORMAT_ARG, i));
            return likes;
        }
    }
}