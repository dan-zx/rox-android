package com.grayfox.android.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;

import com.grayfox.android.R;
import com.grayfox.android.client.model.User;
import com.grayfox.android.client.task.GetSelfUserAsyncTask;
import com.grayfox.android.fragment.ExploreByFriendsLikesFragment;
import com.grayfox.android.fragment.ExploreByLikesFragment;
import com.grayfox.android.fragment.UserProfileFragment;
import com.grayfox.android.widget.drawer.DrawerHeader;
import com.grayfox.android.widget.drawer.DrawerItem;
import com.grayfox.android.widget.drawer.DrawerItemAdapter;
import com.grayfox.android.widget.drawer.DrawerOption;
import com.grayfox.android.widget.drawer.DrawerOptionHeader;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActionBarActivity {

    private static final String FRAGMENT_TAG = "CURRENT_FRAGMENT";
    private static final String CURRENT_TITLE_KEY = "CURRENT_TITLE";
    private static final String CURRENT_SELECTED_OPTION_KEY = "CURRENT_OPTION_SELECTED";
    private static final String USER_KEY = "USER";

    @InjectView(R.id.drawer_options) private RecyclerView drawerOptions;
    @InjectView(R.id.drawer_layout)  private DrawerLayout drawerLayout;
    @InjectView(R.id.toolbar)        private Toolbar toolbar;

    private int currentTitleId;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerItemAdapter drawerItemAdapter;
    private User user;
    private List<DrawerItem> drawerItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupNavigationDrawer();
        if (savedInstanceState == null) {
            setupFragment(new ExploreByLikesFragment());
            setTitle(R.string.drawer_explore_by_your_likes_option);
            drawerItemAdapter.setSelectedPosition(3);
            new GetSelfUserTask(this).execute();
        } else {
            setTitle(savedInstanceState.getInt(CURRENT_TITLE_KEY));
            drawerItemAdapter.setSelectedPosition(savedInstanceState.getInt(CURRENT_SELECTED_OPTION_KEY));
            user = (User) savedInstanceState.getSerializable(USER_KEY);
            if (user == null) new GetSelfUserTask(this).execute();
            else onGetSelfUserSuccess(user);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_TITLE_KEY, currentTitleId);
        outState.putSerializable(USER_KEY, user);
        outState.putInt(CURRENT_SELECTED_OPTION_KEY, drawerItemAdapter.getSelectedPosition());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void setTitle(int titleId) {
        currentTitleId = titleId;
        super.setTitle(titleId);
    }

    private void setupNavigationDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);
        drawerLayout.setStatusBarBackground(R.color.primary_dark);
        drawerLayout.setDrawerListener(drawerToggle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setupDrawerMenu();
    }

    private void setupDrawerMenu() {
        drawerItems = Arrays.asList(
                new DrawerHeader(),
                new DrawerItem(DrawerItem.Type.DIVIDER),
                new DrawerOptionHeader().setNameRes(R.string.drawer_explore_header),
                new DrawerOption().setUnselectedIconRes(R.drawable.ic_person_unselected).setSelectedIconRes(R.drawable.ic_person_selected).setNameRes(R.string.drawer_explore_by_your_likes_option),
                new DrawerOption().setUnselectedIconRes(R.drawable.ic_group_unselected).setSelectedIconRes(R.drawable.ic_group_selected).setNameRes(R.string.drawer_explore_by_your_friends_likes_option),
                new DrawerItem(DrawerItem.Type.DIVIDER),
                new DrawerOption().setUnselectedIconRes(R.drawable.ic_settings).setNameRes(R.string.drawer_settings_option)
        );
        drawerItemAdapter = new DrawerItemAdapter(drawerItems);
        drawerItemAdapter.setOnItemClickListener(new DrawerItemAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                onDrawerMenuSelected(position);
            }
        });
        drawerOptions.setHasFixedSize(true);
        drawerOptions.setLayoutManager(new LinearLayoutManager(this));
        drawerOptions.setAdapter(drawerItemAdapter);
    }

    private void setupFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (currentFragment == null || !currentFragment.getClass().equals(fragment.getClass())) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment, FRAGMENT_TAG)
                    .commit();
        }
    }

    private void onDrawerMenuSelected(int position) {
        switch (position) {
            case 0:
                if (user != null) {
                    invalidateOptionsMenu();
                    setTitle(R.string.profile_title);
                    setupFragment(UserProfileFragment.newInstance(user));
                    drawerItemAdapter.setSelectedPosition(-1);
                }
                drawerLayout.closeDrawers();
                break;
            case 3:
                invalidateOptionsMenu();
                setupFragment(new ExploreByLikesFragment());
                setTitle(R.string.drawer_explore_by_your_likes_option);
                drawerItemAdapter.setSelectedPosition(position);
                drawerLayout.closeDrawers();
                break;
            case 4:
                invalidateOptionsMenu();
                setupFragment(new ExploreByFriendsLikesFragment());
                setTitle(R.string.drawer_explore_by_your_friends_likes_option);
                drawerItemAdapter.setSelectedPosition(position);
                drawerLayout.closeDrawers();
                break;
            case 6:
                startActivity(new Intent(this, SettingsActivity.class));
                drawerLayout.closeDrawers();
                break;
        }
    }

    private void onGetSelfUserSuccess(User user) {
        this.user = user;
        ((DrawerHeader)drawerItems.get(0)).setUser(user);
        drawerItemAdapter.notifyDataSetChanged();
    }

    private static class GetSelfUserTask extends GetSelfUserAsyncTask {

        private WeakReference<MainActivity> reference;

        private GetSelfUserTask(MainActivity activity) {
            super(activity.getApplicationContext());
            reference = new WeakReference<>(activity);
        }

        @Override
        protected void onSuccess(User user) throws Exception {
            super.onSuccess(user);
            MainActivity activity = reference.get();
            if (activity != null) activity.onGetSelfUserSuccess(user);
        }
    }
}