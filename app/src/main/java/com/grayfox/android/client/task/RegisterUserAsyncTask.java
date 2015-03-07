package com.grayfox.android.client.task;

import android.content.Context;

import com.grayfox.android.app.dao.AccessTokenDao;
import com.grayfox.android.client.UsersApi;

import javax.inject.Inject;

public abstract class RegisterUserAsyncTask extends NetworkAsyncTask<Void> {

    @Inject private AccessTokenDao accessTokenDao;
    @Inject private UsersApi usersApi;

    private String foursquareAuthorizationCode;

    protected RegisterUserAsyncTask(Context context) {
        super(context);
    }

    public RegisterUserAsyncTask foursquareAuthorizationCode(String foursquareAuthorizationCode) {
        this.foursquareAuthorizationCode = foursquareAuthorizationCode;
        return this;
    }

    @Override
    public Void call() throws Exception {
        String accessToken = usersApi.awaitAccessToken(foursquareAuthorizationCode);
        accessTokenDao.saveOrUpdateAccessToken(accessToken);
        return null;
    }

    @Override
    protected void onException(Exception e) throws RuntimeException {
        super.onException(e);
        accessTokenDao.deleteAccessToken();
    }
}