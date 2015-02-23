package com.grayfox.android.client.task;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.grayfox.android.R;
import com.grayfox.android.http.RequestBuilder;

import roboguice.util.RoboAsyncTask;

abstract class NetworkAsyncTask<T> extends RoboAsyncTask<T> {

    private boolean isActive;

    protected NetworkAsyncTask(Context context) {
        super(context);
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void request() {
        if (isConnected()) {
            isActive = true;
            execute();
        } else {
            isActive = false;
            Toast.makeText(getContext(), R.string.network_unavailable, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onException(Exception e) throws RuntimeException {
        isActive = false;
        if (e instanceof RequestBuilder.RequestException) onRequestException(e.getCause());
    }

    protected void onRequestException(Throwable e) {
        isActive = false;
        Toast.makeText(getContext(), R.string.network_request_error, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSuccess(T t) throws Exception {
        isActive = false;
    }

    @Override
    protected void onFinally() throws RuntimeException {
        isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }
}