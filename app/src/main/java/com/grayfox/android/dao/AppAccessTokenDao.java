package com.grayfox.android.dao;

import com.google.inject.ImplementedBy;

import com.grayfox.android.dao.impl.sharedpreferences.AppAccessTokenSharedPreferencesDao;

@ImplementedBy(AppAccessTokenSharedPreferencesDao.class)
public interface AppAccessTokenDao {

    String fetchAccessToken();
    void saveAccessToken(String accessToken);
    void deleteAccessToken();
}