package com.grayfox.android.app.widget.drawer;

public class DrawerOption extends DrawerItem  {

    private int nameRes;
    private int unselectedIconRes;
    private int selectedIconRes;

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

    public int getUnselectedIconRes() {
        return unselectedIconRes;
    }

    public DrawerOption setUnselectedIconRes(int unselectedIconRes) {
        this.unselectedIconRes = unselectedIconRes;
        return this;
    }

    public int getSelectedIconRes() {
        return selectedIconRes;
    }

    public DrawerOption setSelectedIconRes(int selectedIconRes) {
        this.selectedIconRes = selectedIconRes;
        return this;
    }
}