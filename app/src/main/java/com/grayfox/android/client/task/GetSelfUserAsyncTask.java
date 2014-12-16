package com.grayfox.android.client.task;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.grayfox.android.client.AppUsersApi;
import com.grayfox.android.client.model.User;
import com.grayfox.android.dao.AppAccessTokenDao;
import com.grayfox.android.dao.UserDao;

import javax.inject.Inject;

import roboguice.util.RoboAsyncTask;

public abstract class GetSelfUserAsyncTask extends RoboAsyncTask<User> {

    @Inject private AppAccessTokenDao appAccessTokenDao;
    @Inject private AppUsersApi appUsersApi;
    @Inject private UserDao userDao;

    @Inject
    protected GetSelfUserAsyncTask(Context context) {
        super(context);
    }

    @Override
    public User call() throws Exception {
        if (isConnected()) {
            User user = appUsersApi.awaitSelfUser(appAccessTokenDao.fetchAccessToken());
            userDao.saveOrUpdate(user);
            return user;
        }
        else return userDao.fetchCurrent();
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}