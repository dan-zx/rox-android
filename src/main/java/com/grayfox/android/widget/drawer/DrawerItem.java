package com.grayfox.android.widget.drawer;

public class DrawerItem {

    public static enum Type {HEADER, DIVIDER, OPTION, OPTION_HEADER}

    private final Type type;

    private boolean isSelected;

    public DrawerItem(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public DrawerItem setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        return this;
    }
}