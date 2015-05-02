package com.grayfox.android.app.widget;

public class DrawerItem {

    public enum Type {HEADER, DIVIDER, OPTION}

    private final Type type;

    public DrawerItem(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}