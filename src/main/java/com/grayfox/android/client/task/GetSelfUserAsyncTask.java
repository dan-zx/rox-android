package com.grayfox.android.client.task;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.grayfox.android.client.UsersApi;
import com.grayfox.android.client.model.User;
import com.grayfox.android.dao.AccessTokenDao;
import com.grayfox.android.dao.UserDao;

import roboguice.util.RoboAsyncTask;

import javax.inject.Inject;

public abstract class GetSelfUserAsyncTask extends RoboAsyncTask<User> {

    @Inject private AccessTokenDao accessTokenDao;
    @Inject private UsersApi usersApi;
    @Inject private UserDao userDao;

    @Inject
    protected GetSelfUserAsyncTask(Context context) {
        super(context);
    }

    @Override
    public User call() throws Exception {
        if (isConnected()) {
            User user = usersApi.awaitSelfUser(accessTokenDao.fetchAccessToken());
            if (user != null) {
                userDao.saveOrUpdate(user);
                return user;
            }
        }
        return userDao.fetchCurrent();
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}