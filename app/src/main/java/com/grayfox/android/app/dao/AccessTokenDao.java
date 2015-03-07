package com.grayfox.android.app.dao;

import com.google.inject.ImplementedBy;

import com.grayfox.android.app.dao.impl.sharedpreferences.AccessTokenSharedPreferencesDao;

@ImplementedBy(AccessTokenSharedPreferencesDao.class)
public interface AccessTokenDao {

    String fetchAccessToken();

    void saveOrUpdateAccessToken(String accessToken);

    void deleteAccessToken();
}