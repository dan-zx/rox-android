package com.grayfox.android.client.task;

import android.content.Context;

import com.grayfox.android.app.dao.AccessTokenDao;
import com.grayfox.android.client.UsersApi;
import com.grayfox.android.client.model.Category;
import com.grayfox.android.client.model.UpdateResult;

import javax.inject.Inject;

public class PostAddLikeAsyncTask extends NetworkAsyncTask<UpdateResult> {

    @Inject private AccessTokenDao accessTokenDao;
    @Inject private UsersApi usersApi;

    private Category like;

    public PostAddLikeAsyncTask(Context context) {
        super(context);
    }

    public PostAddLikeAsyncTask like(Category like) {
        this.like = like;
        return this;
    }

    @Override
    public UpdateResult call() throws Exception {
        return usersApi.postAddLike(accessTokenDao.fetchAccessToken(), like);
    }
}