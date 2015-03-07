package com.grayfox.android.app.dao;

import com.google.inject.ImplementedBy;

import com.grayfox.android.app.dao.impl.sqlite.UserSqliteDao;
import com.grayfox.android.client.model.User;

@ImplementedBy(UserSqliteDao.class)
public interface UserDao {

    User fetchCurrent();

    void saveOrUpdate(User user);

    void deleteCurrent();
}