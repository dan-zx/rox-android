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
package com.grayfox.android.app.task;

import android.content.Context;
import android.widget.Toast;

import roboguice.util.RoboAsyncTask;

public abstract class BaseAsyncTask<T> extends RoboAsyncTask<T> {

    private boolean isActive;

    protected BaseAsyncTask(Context context) {
        super(context);
    }

    @Override
    public void execute() {
        isActive = true;
        super.execute();
    }

    @Override
    protected void onSuccess(T t) throws Exception {
        isActive = true;
    }

    @Override
    protected void onException(Exception e) throws RuntimeException {
        isActive = false;
        Toast.makeText(getContext().getApplicationContext(),
                e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onFinally() throws RuntimeException {
        isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }

    protected void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}