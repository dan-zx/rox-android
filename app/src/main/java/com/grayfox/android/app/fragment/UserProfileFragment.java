package com.grayfox.android.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import com.grayfox.android.app.R;
import com.grayfox.android.app.activity.FriendProfileActivity;
import com.grayfox.android.app.dao.AccessTokenDao;
import com.grayfox.android.app.task.NetworkAsyncTask;
import com.grayfox.android.app.widget.CategoryFilterableAdapter;
import com.grayfox.android.app.widget.FriendAdapter;
import com.grayfox.android.app.widget.LikeDeletableAdapter;
import com.grayfox.android.client.UsersApi;
import com.grayfox.android.client.model.Category;
import com.grayfox.android.client.model.UpdateResult;
import com.grayfox.android.client.model.User;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

import javax.inject.Inject;

public class UserProfileFragment extends RoboFragment {

    private static final String USER_ARG = "USER";

    @InjectView(R.id.profile_image) private CircleImageView profileImageView;
    @InjectView(R.id.user_name)     private TextView userNameTextView;
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
            task = new CompleteUserTask().currentUser(user);
            task.request();
        } else {
            if (task != null && task.isActive()) onPreExecuteTask();
            else if (user != null) {
                if (user.getLikes() != null && user.getFriends() != null) {
                    onCompleteUser(user);
                    onTaskFinally();
                } else {
                    task = new CompleteUserTask().currentUser(user);
                    task.request();
                }
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
        progressBar.setVisibility(View.VISIBLE);
    }

    private void onCompleteUser(User user) {
        this.user = user;
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

    private class CompleteUserTask extends NetworkAsyncTask<User> {

        @Inject private AccessTokenDao accessTokenDao;
        @Inject private UsersApi usersApi;

        private User user;

        private CompleteUserTask() {
            super(getActivity().getApplicationContext());
        }

        public CompleteUserTask currentUser(User user) {
            this.user = user;
            return this;
        }

        @Override
        protected void onPreExecute() throws Exception {
            super.onPreExecute();
            onPreExecuteTask();
        }

        @Override
        public User call() throws Exception {
            user.setFriends(usersApi.awaitSelfUserFriends(accessTokenDao.fetchAccessToken()));
            user.setLikes(usersApi.awaitSelfUserLikes(accessTokenDao.fetchAccessToken()));
            return user;
        }

        @Override
        protected void onSuccess(User user) throws Exception {
            super.onSuccess(user);
            onCompleteUser(user);
        }

        @Override
        protected void onFinally() throws RuntimeException {
            super.onFinally();
            onTaskFinally();
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

    public static class FriendsFragment extends RoboFragment {

        private static final String FRIENDS_LENGTH_ARG = "FRIENDS_LENGTH";
        private static final String FRIENDS_FORMAT_ARG = "FRIEND_%d";

        @InjectView(R.id.no_friends)  private TextView noFriendsTextView;
        @InjectView(R.id.friend_list) private RecyclerView friendsListView;

        private static FriendsFragment newInstance(User[] friends) {
            FriendsFragment fragment = new FriendsFragment();
            Bundle args = new Bundle();
            args.putInt(FRIENDS_LENGTH_ARG, friends.length);
            for (int i = 0; i < friends.length; i++) args.putSerializable(String.format(FRIENDS_FORMAT_ARG, i), friends[i]);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_self_friends, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            friendsListView.setHasFixedSize(true);
            friendsListView.setLayoutManager(new LinearLayoutManager(getActivity()));
            final User[] friends = getFriendsArg();
            if (friends.length == 0) {
                noFriendsTextView.setText(R.string.no_friends_found);
                friendsListView.setVisibility(View.GONE);
            } else {
                noFriendsTextView.setVisibility(View.GONE);
                FriendAdapter adapter = new FriendAdapter(friends);
                adapter.setOnItemClickListener(new FriendAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(int position) {
                        startActivity(FriendProfileActivity.getIntent(getActivity(), friends[position]));
                    }
                });
                friendsListView.setAdapter(adapter);
            }
        }

        private User[] getFriendsArg() {
            User[] friends = new User[getArguments().getInt(FRIENDS_LENGTH_ARG)];
            for (int i = 0; i < friends.length; i++) friends[i] = (User) getArguments().getSerializable(String.format(FRIENDS_FORMAT_ARG, i));
            return friends;
        }
    }

    public static class LikesFragment extends RoboFragment {

        private static final String LIKES_LENGTH_ARG = "LIKES_LENGTH";
        private static final String LIKES_FORMAT_ARG = "LIKE_%d";

        @InjectView(R.id.like_search) private AutoCompleteTextView likeSearchView;
        @InjectView(R.id.like_list)   private RecyclerView likeList;
        @InjectView(R.id.no_likes)    private TextView noLikesTextView;

        @Inject private InputMethodManager inputMethodManager;

        private CategoryFilterableAdapter categoryAdapter;
        private LikeDeletableAdapter likeAdapter;

        private static LikesFragment newInstance(Category[] likes) {
            LikesFragment fragment = new LikesFragment();
            Bundle args = new Bundle();
            args.putInt(LIKES_LENGTH_ARG, likes.length);
            for (int i = 0; i < likes.length; i++) args.putSerializable(String.format(LIKES_FORMAT_ARG, i), likes[i]);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_self_likes, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            categoryAdapter = new CategoryFilterableAdapter(getActivity());
            likeSearchView.setAdapter(categoryAdapter);
            likeSearchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    onSuggestionClicked(position);
                }
            });
            likeList.setLayoutManager(new LinearLayoutManager(getActivity()));
            Category[] likes = getLikesArg();
            if (likes.length == 0) {
                noLikesTextView.setText(R.string.no_likes_found);
                likeList.setVisibility(View.GONE);
            } else {
                noLikesTextView.setVisibility(View.GONE);
                likeAdapter = new LikeDeletableAdapter(likes);
                likeAdapter.setOnRemoveLikeListener(new LikeDeletableAdapter.OnRemoveLikeListener() {
                    @Override
                    public void onRemove(Category like) {
                        onRemoveLike(like);
                    }
                });
                likeList.setAdapter(likeAdapter);
            }
        }

        private void onSuggestionClicked(int position) {
            Category category = categoryAdapter.getItem(position);
            likeSearchView.setText(null);
            inputMethodManager.hideSoftInputFromWindow(likeSearchView.getWindowToken(), 0);
            if (likeAdapter.add(category)) {
                likeAdapter.notifyDataSetChanged();
                new AddLikeTask(getActivity().getApplicationContext())
                        .like(category)
                        .request();
            }
        }

        private void onRemoveLike(Category like) {
            new RemoveLikeTask(getActivity().getApplicationContext())
                    .like(like)
                    .request();
        }

        private Category[] getLikesArg() {
            Category[] likes = new Category[getArguments().getInt(LIKES_LENGTH_ARG)];
            for (int i = 0; i < likes.length; i++) likes[i] = (Category) getArguments().getSerializable(String.format(LIKES_FORMAT_ARG, i));
            return likes;
        }

        private static class AddLikeTask extends NetworkAsyncTask<UpdateResult> {

            @Inject private AccessTokenDao accessTokenDao;
            @Inject private UsersApi usersApi;

            private Category like;

            private AddLikeTask(Context context) {
                super(context);
            }

            private AddLikeTask like(Category like) {
                this.like = like;
                return this;
            }

            @Override
            public UpdateResult call() throws Exception {
                return usersApi.awaitAddLike(accessTokenDao.fetchAccessToken(), like);
            }
        }

        private static class RemoveLikeTask extends NetworkAsyncTask<UpdateResult> {

            @Inject private AccessTokenDao accessTokenDao;
            @Inject private UsersApi usersApi;

            private Category like;

            private RemoveLikeTask(Context context) {
                super(context);
            }

            private RemoveLikeTask like(Category like) {
                this.like = like;
                return this;
            }

            @Override
            public UpdateResult call() throws Exception {
                return usersApi.awaitRemoveLike(accessTokenDao.fetchAccessToken(), like);
            }
        }
    }
}