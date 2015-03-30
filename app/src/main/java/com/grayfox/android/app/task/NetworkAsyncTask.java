package com.grayfox.android.app.task;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.grayfox.android.app.R;

public abstract class NetworkAsyncTask<T> extends BaseAsyncTask<T> {

    protected NetworkAsyncTask(Context context) {
        super(context);
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void request() {
        if (isConnected()) execute();
        else {
            setActive(false);
            Toast.makeText(getContext(), R.string.network_unavailable, Toast.LENGTH_LONG).show();
        }
    }
}