package com.grayfox.android.app.widget.drawer;

public class DrawerOptionHeader extends DrawerItem  {

    private int nameRes;

    public DrawerOptionHeader() {
        super(Type.OPTION_HEADER);
    }

    public int getNameRes() {
        return nameRes;
    }

    public DrawerOptionHeader setNameRes(int nameRes) {
        this.nameRes = nameRes;
        return this;
    }
}