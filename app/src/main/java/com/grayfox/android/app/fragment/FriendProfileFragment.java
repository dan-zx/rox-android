/*
 * Copyright 2014-2015 Daniel Pedraza-Arcega
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.grayfox.android.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import com.grayfox.android.app.R;
import com.grayfox.android.app.dao.AccessTokenDao;
import com.grayfox.android.app.task.NetworkAsyncTask;
import com.grayfox.android.app.widget.LikeAdapter;
import com.grayfox.android.client.UsersApi;
import com.grayfox.android.client.model.Category;
import com.grayfox.android.client.model.User;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

import javax.inject.Inject;

public class FriendProfileFragment extends RoboFragment {

    private static final String FRIEND_ARG = "FRIEND";

    @InjectView(R.id.profile_image)  private CircleImageView profileImageView;
    @InjectView(R.id.user_name)      private TextView userNameTextView;
    @InjectView(R.id.pager_strip)    private PagerSlidingTabStrip pagerStrip;
    @InjectView(R.id.view_pager)     private ViewPager viewPager;
    @InjectView(R.id.progress_bar)   private ProgressBar progressBar;
    @InjectView(R.id.likes_list)     private RecyclerView likesList;
    @InjectView(R.id.no_likes_found) private TextView noLikesFoundText;

    private GetFriendLikesTask task;
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
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {

            @Override
            public Fragment getItem(int i) {
                return new Fragment();
            }

            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0: return getString(R.string.profile_user_likes_tab);
                    default: return null;
                }
            }
        });
        pagerStrip.setViewPager(viewPager);
        likesList.setHasFixedSize(true);
        likesList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpUserArg();
        if (savedInstanceState == null) {
            task = new GetFriendLikesTask().foursquareId(friend.getFoursquareId());
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
        String userFullName = friend.getLastName() == null || friend.getLastName().trim().isEmpty() ? friend.getName() : friend.getName() + " " + friend.getLastName();
        userNameTextView.setText(userFullName);
        Picasso.with(getActivity())
                .load(friend.getPhotoUrl())
                .placeholder(R.drawable.ic_contact_picture)
                .into(profileImageView);
    }

    private void onPreExecuteFriendTask() {
        progressBar.setVisibility(View.VISIBLE);
        likesList.setVisibility(View.GONE);
        noLikesFoundText.setVisibility(View.GONE);
    }

    private void onGotFriendLikes(Category[] likes) {
        friend.setLikes(likes);
        if (likes == null || likes.length == 0) noLikesFoundText.setVisibility(View.VISIBLE);
        else {
            likesList.setAdapter(new LikeAdapter(likes));
            noLikesFoundText.setVisibility(View.GONE);
            likesList.setVisibility(View.VISIBLE);
        }
    }

    private void onFriendTaskFinally() {
        progressBar.setVisibility(View.GONE);
    }

    private User getFriendArg() {
        return (User) getArguments().getSerializable(FRIEND_ARG);
    }

    private class GetFriendLikesTask extends NetworkAsyncTask<Category[]> {

        @Inject private AccessTokenDao accessTokenDao;
        @Inject private UsersApi usersApi;

        private String foursquareId;

        private GetFriendLikesTask() {
            super(getActivity().getApplicationContext());
        }

        private GetFriendLikesTask foursquareId(String foursquareId) {
            this.foursquareId = foursquareId;
            return this;
        }

        @Override
        protected void onPreExecute() throws Exception {
            super.onPreExecute();
            onPreExecuteFriendTask();
        }

        @Override
        public Category[] call() throws Exception {
            return usersApi.awaitUserLikes(accessTokenDao.fetchAccessToken(), foursquareId);
        }

        @Override
        protected void onSuccess(Category[] likes) throws Exception {
            super.onSuccess(likes);
            onGotFriendLikes(likes);
        }

        @Override
        protected void onFinally() throws RuntimeException {
            super.onFinally();
            onFriendTaskFinally();
        }
    }
}