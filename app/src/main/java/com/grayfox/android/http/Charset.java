package com.grayfox.android.http;

public enum Charset {

    ISO_8859_1, US_ASCII, UTF_8, UTF_16, UTF_16BE, UTF_16LE;

    public String getValue() {
        return name().replace('_', '-');
    }
}