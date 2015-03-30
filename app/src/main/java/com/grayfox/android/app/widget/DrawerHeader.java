package com.grayfox.android.app.widget;

import com.grayfox.android.client.model.User;

public class DrawerHeader extends DrawerItem {

    private User user;

    public DrawerHeader() {
        super(Type.HEADER);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}