package com.grayfox.android.dao;

import com.google.inject.ImplementedBy;
import com.grayfox.android.client.model.User;
import com.grayfox.android.dao.impl.sqlite.UserSqliteDao;

@ImplementedBy(UserSqliteDao.class)
public interface UserDao {

    User fetchCurrent();
    void saveOrUpdate(User user);
    void deleteCurrent();
}