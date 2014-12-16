package com.grayfox.android.widget;

public class DrawerItem {

    private final int icon;
    private final int text;

    public DrawerItem(int icon, int text) {
        this.icon = icon;
        this.text = text;
    }

    public int getIcon() {
        return icon;
    }

    public int getText() {
        return text;
    }
}