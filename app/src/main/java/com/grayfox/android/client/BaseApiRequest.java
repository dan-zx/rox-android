package com.grayfox.android.client;

import android.content.Context;

import java.util.Locale;

abstract class BaseApiRequest {

    private final Context context;

    private String accessToken;

    protected BaseApiRequest(Context context) {
        this.context = context;
    }

    public BaseApiRequest accessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    protected String getAccessToken() {
        return accessToken;
    }

    protected Context getContext() {
        return context;
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
}