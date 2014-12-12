package com.grayfox.android.client;

import android.content.Context;

import java.util.Locale;

abstract class BaseApi {

    private final Context context;

    private String accessToken;

    protected BaseApi(Context context) {
        this.context = context;
    }

    protected Context getContext() {
        return context;
    }

    protected String getString(int resId) {
        return context.getString(resId);
    }

    protected String getClientAcceptLanguage() {
        return Locale.getDefault().toString().replace('_', '-');
    }
}