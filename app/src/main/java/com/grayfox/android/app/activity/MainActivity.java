package com.grayfox.android.app.activity;

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

import com.grayfox.android.app.R;
import com.grayfox.android.app.fragment.ExploreFragment;
import com.grayfox.android.app.fragment.UserProfileFragment;
import com.grayfox.android.app.widget.drawer.DrawerHeader;
import com.grayfox.android.app.widget.drawer.DrawerItem;
import com.grayfox.android.app.widget.drawer.DrawerItemAdapter;
import com.grayfox.android.app.widget.drawer.DrawerOption;
import com.grayfox.android.client.model.User;
import com.grayfox.android.client.task.GetSelfUserAsyncTask;

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
            setupFragment(new ExploreFragment());
            setTitle(R.string.explore_title);
            drawerItemAdapter.setSelectedPosition(2);
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
                new DrawerOption().setUnselectedIconRes(R.drawable.ic_search_black_24dp).setSelectedIconRes(R.drawable.ic_search_white_24dp).setNameRes(R.string.drawer_explore_option),
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
            case 2:
                invalidateOptionsMenu();
                setupFragment(new ExploreFragment());
                setTitle(R.string.explore_title);
                drawerItemAdapter.setSelectedPosition(position);
                drawerLayout.closeDrawers();
                break;
            case 4:
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