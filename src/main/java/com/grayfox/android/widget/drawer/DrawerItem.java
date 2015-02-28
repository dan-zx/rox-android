package com.grayfox.android.widget.drawer;

public class DrawerItem {

    public static enum Type {HEADER, DIVIDER, OPTION, OPTION_HEADER}

    private final Type type;

    public DrawerItem(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}