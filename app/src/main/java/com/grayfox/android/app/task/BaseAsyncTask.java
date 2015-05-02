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