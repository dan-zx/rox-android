package com.grayfox.android.client;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Locale;

public abstract class BaseApiRequest {

    private final Context context;

    private String accessToken;

    protected BaseApiRequest(Context context) {
        this.context = context;
    }

    protected Context getContext() {
        return context;
    }

    protected boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    protected String getString(int resId) {
        return context.getString(resId);
    }

    protected String getString(int resId, Object... formatArgs) {
        return context.getString(resId, formatArgs);
    }

    protected String getClientAcceptLanguage() {
        return Locale.getDefault().toString().replace('_', '-');
    }

    public static interface RequestCallback<T> {

        void onSuccess(T t);
        void onFailure(String reason);
    }
}