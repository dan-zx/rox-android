package com.grayfox.android.http;

public abstract class RequestBuilder {

    public static final int DEFAULT_TIMEOUT = 30000;

    public static RequestBuilder newInstance(String url) {
        return new UrlConnectionRequestBuilder(url);
    }

    public abstract RequestBuilder setTimeout(int timeout);
    public abstract RequestBuilder setMethod(Method method);
    public abstract RequestBuilder setData(String data);
    public abstract RequestBuilder addFormParam(String name, String value);
    public abstract RequestBuilder setHeader(Header header, String value);
    public abstract Integer make() throws RequestException;
    public abstract String makeForResult() throws RequestException;

    public static class RequestException extends RuntimeException {

        public RequestException(String message) {
            super(message);
        }

        public RequestException(String message, Throwable t) {
            super(message, t);
        }
    }
}