package com.grayfox.android.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.grayfox.android.R;
import com.grayfox.android.client.model.User;
import com.grayfox.android.client.task.GetSelfUserAsyncTask;
import com.grayfox.android.fragment.ExploreByFriendsLikesFragment;
import com.grayfox.android.fragment.ExploreByLikesFragment;
import com.grayfox.android.widget.DrawerItem;
import com.grayfox.android.widget.DrawerItemAdapter;

import com.squareup.picasso.Picasso;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActionBarActivity {

    private static final String FRAGMENT_TAG = "CURRENT_FRAGMENT";
    private static final String CURRENT_TITLE_KEY = "CURRENT_TITLE";

    @InjectView(R.id.drawer_options) private ListView drawerOptions;
    @InjectView(R.id.drawer_layout)  private DrawerLayout drawerLayout;
    @InjectView(R.id.toolbar)        private Toolbar toolbar;

    private int currentTitleId;
    private ActionBarDrawerToggle drawerToggle;
    private User user;
    private TextView userNameText;
    private ImageView userPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupNavigationDrawer();
        if (savedInstanceState == null) {
            setupFragment(new ExploreByLikesFragment());
            setTitle(R.string.drawer_explore_by_your_likes_option);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_TITLE_KEY, currentTitleId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setTitle(savedInstanceState.getInt(CURRENT_TITLE_KEY));
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
        drawerLayout.setStatusBarBackground(R.color.primary_dark_color);
        drawerLayout.setDrawerListener(drawerToggle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setupDrawerHeader();
        setupDrawerMenu();
    }

    private void setupDrawerHeader() {
        View headerView = getLayoutInflater().inflate(R.layout.drawer_header, null);
        userNameText = (TextView) headerView.findViewById(R.id.user_name_text);
        userPicture = (ImageView) headerView.findViewById(R.id.profile_image);
        drawerOptions.addHeaderView(headerView);
        new GetSelfUserTask(this).execute();
    }

    private void setupDrawerMenu() {
        List<DrawerItem> drawerItems = new ArrayList<>(2);
        drawerItems.add(new DrawerItem(R.drawable.ic_person, R.string.drawer_explore_by_your_likes_option));
        drawerItems.add(new DrawerItem(R.drawable.ic_group, R.string.drawer_explore_by_your_friends_likes_option));
        drawerItems.add(new DrawerItem(R.drawable.ic_settings, R.string.drawer_settings_option));
        drawerOptions.setAdapter(new DrawerItemAdapter(drawerItems));
        drawerOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onDrawerMenuSelected(position);
            }
        });
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
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://foursquare.com/users/" + user.getFoursquareId()));
                    startActivity(intent);
                }
                break;
            case 1:
                supportInvalidateOptionsMenu();
                setupFragment(new ExploreByLikesFragment());
                setTitle(R.string.drawer_explore_by_your_likes_option);
                break;
            case 2:
                supportInvalidateOptionsMenu();
                setupFragment(new ExploreByFriendsLikesFragment());
                setTitle(R.string.drawer_explore_by_your_friends_likes_option);
                break;
            case 3:
                //setupFragment(new SettingsFragment());
                //setTitle(R.string.drawer_settings_option);
                break;
        }
        drawerLayout.closeDrawers();
    }

    private void onGetSelfUserSuccess(User user) {
        this.user = user;
        if (user != null) {
            String userFullName = user.getLastName() == null || user.getLastName().trim().isEmpty() ? user.getName() : new StringBuilder().append(user.getName()).append(" ").append(user.getLastName()).toString();
            userNameText.setText(userFullName);
            Picasso.with(this)
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.ic_contact_picture)
                    .into(userPicture);
        }
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