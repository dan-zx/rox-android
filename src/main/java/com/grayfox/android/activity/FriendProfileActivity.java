package com.grayfox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.grayfox.android.client.model.User;
import com.grayfox.android.fragment.FriendProfileFragment;

import roboguice.activity.RoboActionBarActivity;

public class FriendProfileActivity extends RoboActionBarActivity {

    private static final String FRIEND_ARG = "FRIEND";

    public static Intent getIntent(Context context, User friend) {
        Intent intent = new Intent(context, FriendProfileActivity.class);
        intent.putExtra(FRIEND_ARG, friend);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            User friend = (User) getIntent().getExtras().getSerializable(FRIEND_ARG);
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, FriendProfileFragment.newInstance(friend))
                    .commit();
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