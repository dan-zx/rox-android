package com.grayfox.android.client.task;

import android.content.Context;

import com.grayfox.android.app.dao.AccessTokenDao;
import com.grayfox.android.client.UsersApi;
import com.grayfox.android.client.model.User;

import javax.inject.Inject;

public abstract class CompleteUserAsyncTask extends NetworkAsyncTask<User> {

    @Inject private AccessTokenDao accessTokenDao;
    @Inject private UsersApi usersApi;

    private User user;

    protected CompleteUserAsyncTask(Context context) {
        super(context);
    }

    public CompleteUserAsyncTask currentUser(User user) {
        this.user = user;
        return this;
    }

    @Override
    public User call() throws Exception {
        user.setFriends(usersApi.awaitSelfUserFriends(accessTokenDao.fetchAccessToken()));
        user.setLikes(usersApi.awaitSelfUserLikes(accessTokenDao.fetchAccessToken()));
        return user;
    }
}