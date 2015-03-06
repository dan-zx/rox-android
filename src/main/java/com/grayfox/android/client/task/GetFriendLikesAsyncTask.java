package com.grayfox.android.client.task;

import android.content.Context;

import com.grayfox.android.client.UsersApi;
import com.grayfox.android.client.model.Category;
import com.grayfox.android.dao.AccessTokenDao;

import javax.inject.Inject;

public abstract class GetFriendLikesAsyncTask extends NetworkAsyncTask<Category[]> {

    @Inject private AccessTokenDao accessTokenDao;
    @Inject private UsersApi usersApi;

    private String foursquareId;

    protected GetFriendLikesAsyncTask(Context context) {
        super(context);
    }

    public GetFriendLikesAsyncTask foursquareId(String foursquareId) {
        this.foursquareId = foursquareId;
        return this;
    }

    @Override
    public Category[] call() throws Exception {
        return usersApi.awaitFriendLikes(accessTokenDao.fetchAccessToken(), foursquareId);
    }
}