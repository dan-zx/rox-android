package com.grayfox.android.client;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.grayfox.android.client.model.Result;

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

    protected <T> Result<T> parse(String json, Class<T> responseClass) {
        JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
        Result.ErrorResponse error = new Gson().fromJson(obj.get("error"), Result.ErrorResponse.class);
        T response = new Gson().fromJson(obj.get("response"), responseClass);
        Result<T> result = new Result<>();
        result.setError(error);
        result.setResponse(response);
        return result;
    }

    protected <T> Result<T> parse(String json, TypeToken<T> type) {
        JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
        Result.ErrorResponse error = new Gson().fromJson(obj.get("error"), Result.ErrorResponse.class);
        T response = new Gson().fromJson(obj.get("response"), type.getType());
        Result<T> result = new Result<>();
        result.setError(error);
        result.setResponse(response);
        return result;
    }
}