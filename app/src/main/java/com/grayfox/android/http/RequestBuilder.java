package com.grayfox.android.http;

import android.os.Build;

public abstract class RequestBuilder {

    public abstract RequestBuilder setTimeout(int timeout);
    public abstract RequestBuilder setMethod(Method method);
    public abstract RequestBuilder setData(String data);
    public abstract RequestBuilder addFormParam(String name, String value);
    public abstract RequestBuilder setHeader(Header header, String value);
    public abstract Integer make();
    public abstract String makeForResult();

    public static RequestBuilder newInstance(String url) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) return new UrlConnectionRequestBuilder(url);
        else return new HttpClientRequestBuilder(url);
    }
}