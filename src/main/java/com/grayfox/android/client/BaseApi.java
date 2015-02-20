package com.grayfox.android.client;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import com.grayfox.android.client.model.ApiResponse;

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

    protected <T> ApiResponse<T> parse(String json, Class<T> responseClass) {
        JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
        ApiResponse.ErrorResponse error = new Gson().fromJson(obj.get("error"), ApiResponse.ErrorResponse.class);
        T response = new Gson().fromJson(obj.get("response"), responseClass);
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setError(error);
        apiResponse.setResponse(response);
        return apiResponse;
    }

    protected <T> ApiResponse<T> parse(String json, TypeToken<T> type) {
        JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
        ApiResponse.ErrorResponse error = new Gson().fromJson(obj.get("error"), ApiResponse.ErrorResponse.class);
        T response = new Gson().fromJson(obj.get("response"), type.getType());
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setError(error);
        apiResponse.setResponse(response);
        return apiResponse;
    }
}