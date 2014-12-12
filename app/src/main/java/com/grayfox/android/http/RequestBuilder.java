package com.grayfox.android.http;

import android.os.Build;

public abstract class RequestBuilder {

    public abstract RequestBuilder setTimeout(int timeout);
    public abstract RequestBuilder setMethod(Method method);
    public abstract RequestBuilder setData(String data);
    public abstract RequestBuilder addFormParam(String name, String value);
    public abstract RequestBuilder setHeader(Header header, String value);
    public abstract Integer make() throws RequestException;
    public abstract String makeForResult() throws RequestException;

    public static RequestBuilder newInstance(String url) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) return new UrlConnectionRequestBuilder(url);
        else return new HttpClientRequestBuilder(url);
    }

    public static class RequestException extends RuntimeException {

        public RequestException(String message) {
            super(message);
        }

        public RequestException(String message, Throwable t) {
            super(message, t);
        }
    }
}