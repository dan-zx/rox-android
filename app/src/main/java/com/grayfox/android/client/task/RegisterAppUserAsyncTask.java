package com.grayfox.android.client.task;

import android.content.Context;

import com.grayfox.android.client.AppUsersApi;
import com.grayfox.android.dao.AppAccessTokenDao;

import javax.inject.Inject;

public abstract class RegisterAppUserAsyncTask extends NetworkAsyncTask<Void> {

    @Inject private AppAccessTokenDao appAccessTokenDao;
    @Inject private AppUsersApi appUsersApi;

    private String foursquareAuthorizationCode;

    protected RegisterAppUserAsyncTask(Context context) {
        super(context);
    }

    public RegisterAppUserAsyncTask foursquareAuthorizationCode(String foursquareAuthorizationCode) {
        this.foursquareAuthorizationCode = foursquareAuthorizationCode;
        return this;
    }

    @Override
    public Void call() throws Exception {
        String accessToken = appUsersApi.awaitAccessToken(foursquareAuthorizationCode);
        appAccessTokenDao.saveOrUpdateAccessToken(accessToken);
        return null;
    }

    @Override
    protected void onRequestException(Throwable e) {
        super.onRequestException(e);
        appAccessTokenDao.deleteAccessToken();
    }
}