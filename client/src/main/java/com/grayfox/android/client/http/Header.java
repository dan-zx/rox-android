package com.grayfox.android.client.http;

public enum Header {

    ACCEPT, ACCEPT_CHARSET, ACCEPT_ENCODING, ACCEPT_LANGUAGE, ACCEPT_DATETIME, AUTHORIZATION,
    CACHE_CONTROL, CONNECTION, COOKIE, CONTENT_LENGTH, CONTENT_MD5, CONTENT_TYPE, DATE, EXPECT,
    FROM, HOST, IF_MATCH, IF_MODIFIED_SINCE, IF_NONE_MATCH, IF_RANGE, IF_UNMODIFIED_SINCE,
    MAX_FORWARDS, ORIGIN, PRAGMA, PROXY_AUTHORIZATION, RANGE, REFERER, TE, UPGRADE, USER_AGENT, VIA,
    WARNING;

    public String getValue() {
        return screamingCapsToCapitalizedWords(name());
    }

    private String screamingCapsToCapitalizedWords(String s) {
        String[] words = s.toLowerCase().split("_");
        StringBuilder finalString = new StringBuilder(s.length() + 1);
        for (String word : words) {
            StringBuilder sb = new StringBuilder(word);
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            finalString.append(sb).append("-");
        }
        finalString.setLength(finalString.length() - 1);
        return finalString.toString();
    }
}