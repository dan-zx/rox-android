package com.grayfox.android.client.task;

import android.content.Context;

import com.grayfox.android.client.AppUsersApi;
import com.grayfox.android.client.model.User;
import com.grayfox.android.dao.AppAccessTokenDao;

import javax.inject.Inject;

public abstract class GetSelfUserAsyncTask extends NetworkAsyncTask<User> {

    @Inject private AppAccessTokenDao appAccessTokenDao;
    @Inject private AppUsersApi appUsersApi;

    @Inject
    protected GetSelfUserAsyncTask(Context context) {
        super(context);
    }

    @Override
    public User call() throws Exception {
        return appUsersApi.awaitSelfUser(appAccessTokenDao.fetchAccessToken());
    }
}
