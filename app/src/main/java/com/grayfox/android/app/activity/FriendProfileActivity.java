package com.grayfox.android.app.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.grayfox.android.app.R;
import com.grayfox.android.app.fragment.FriendProfileFragment;
import com.grayfox.android.client.model.User;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.generic_activity_with_toolbar)
public class FriendProfileActivity extends RoboActionBarActivity {

    private static final String FRIEND_ARG = "FRIEND";

    @InjectView(R.id.toolbar) private Toolbar toolbar;

    public static Intent getIntent(Context context, User friend) {
        Intent intent = new Intent(context, FriendProfileActivity.class);
        intent.putExtra(FRIEND_ARG, friend);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        setStatusBarColor(getResources().getColor(R.color.primary_dark));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            User friend = (User) getIntent().getExtras().getSerializable(FRIEND_ARG);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, FriendProfileFragment.newInstance(friend))
                    .commit();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
}