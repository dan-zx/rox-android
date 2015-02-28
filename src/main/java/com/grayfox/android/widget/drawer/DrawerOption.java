package com.grayfox.android.widget.drawer;

public class DrawerOption extends DrawerItem  {

    private int nameRes;
    private int iconRes;

    public DrawerOption() {
        super(Type.OPTION);
    }

    public int getNameRes() {
        return nameRes;
    }

    public DrawerOption setNameRes(int nameRes) {
        this.nameRes = nameRes;
        return this;
    }

    public int getIconRes() {
        return iconRes;
    }

    public DrawerOption setIconRes(int iconRes) {
        this.iconRes = iconRes;
        return this;
    }
}