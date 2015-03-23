package com.grayfox.android.client;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.grayfox.android.client.model.ApiResponse;
import com.grayfox.android.client.http.Charset;
import com.grayfox.android.client.http.ContentType;
import com.grayfox.android.client.http.Header;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Locale;

abstract class BaseApi {

    private static final String TAG = BaseApi.class.getSimpleName();
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private final Context context;

    private OkHttpClient client;

    protected BaseApi(Context context) {
        this.context = context;
        this.client = new OkHttpClient();
    }

    protected String getString(int resId) {
        return context.getString(resId);
    }

    protected <T> T get(String url, Class<T> responseClass) {
        Log.d(TAG, url);
        Request request = buildRequestForJson(url).get().build();
        return callForResult(request, responseClass);
    }

    protected <T> T post(String url, Object payload, Class<T> responseClass) {
        Log.d(TAG, url);
        Request request = buildRequestForJsonWithJsonContent(url)
                .post(RequestBody.create(JSON_MEDIA_TYPE, new Gson().toJson(payload)))
                .build();
        return callForResult(request, responseClass);
    }

    protected <T> T put(String url, Object payload, Class<T> responseClass) {
        Log.d(TAG, url);
        Request request = buildRequestForJsonWithJsonContent(url)
                .put(RequestBody.create(JSON_MEDIA_TYPE, new Gson().toJson(payload)))
                .build();
        return callForResult(request, responseClass);
    }

    protected <T> T delete(String url, Object payload, Class<T> responseClass) {
        Log.d(TAG, url);
        Request request = buildRequestForJsonWithJsonContent(url)
                .method("DELETE", RequestBody.create(JSON_MEDIA_TYPE, new Gson().toJson(payload)))
                .build();
        return callForResult(request, responseClass);
    }

    private Request.Builder buildRequestForJson(String url) {
        return new Request.Builder()
                .url(url)
                .header(Header.ACCEPT.getValue(), ContentType.APPLICATION_JSON.getMimeType())
                .header(Header.ACCEPT_LANGUAGE.getValue(), Locale.getDefault().getLanguage())
                .header(Header.ACCEPT_CHARSET.getValue(), Charset.UTF_8.getValue());
    }

    private Request.Builder buildRequestForJsonWithJsonContent(String url) {
        return buildRequestForJson(url)
                .header(Header.CONTENT_TYPE.getValue(), ContentType.APPLICATION_JSON.getMimeType());
    }

    private <T> T callForResult(Request request, Class<T> responseClass) {
        try {
            Response response = client.newCall(request).execute();
            Log.d(TAG, "Response code -> " + response.code());
            String json = response.body().string();
            if (json != null) {
                JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                ApiResponse.ErrorResponse error = new Gson().fromJson(obj.get("error"), ApiResponse.ErrorResponse.class);
                T responseObject = new Gson().fromJson(obj.get("response"), responseClass);
                ApiResponse<T> apiResponse = new ApiResponse<>();
                apiResponse.setError(error);
                apiResponse.setResponse(responseObject);
                if (apiResponse.getError() == null) return apiResponse.getResponse();
                else {
                    Log.e(TAG, "Response error -> " + apiResponse.getError());
                    throw new ApiException(apiResponse.getError().getErrorMessage());
                }
            } else {
                Log.e(TAG, "Null response");
                throw new ApiException(getString(R.string.grayfox_api_request_error));
            }
        } catch (IOException ex) {
            Log.e(TAG, "Error while making request", ex);
            throw new ApiException(getString(R.string.network_request_error), ex);
        }
    }
}