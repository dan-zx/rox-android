/*
 * Copyright 2014-2015 Daniel Pedraza-Arcega
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.grayfox.android.app.widget;

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