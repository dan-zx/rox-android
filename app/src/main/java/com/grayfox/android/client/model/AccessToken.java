package com.grayfox.android.client.model;

import java.io.Serializable;

public class AccessToken implements Serializable {

    private static final long serialVersionUID = 564037530412089830L;

    private String appAccessToken;

    public String getToken() {
        return appAccessToken;
    }

    public void setToken(String appAccessToken) {
        this.appAccessToken = appAccessToken;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((appAccessToken == null) ? 0 : appAccessToken.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        AccessToken other = (AccessToken) obj;
        if (appAccessToken == null) {
            if (other.appAccessToken != null) return false;
        } else if (!appAccessToken.equals(other.appAccessToken)) return false;
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("AppAccessToken [appAccessToken=").append(appAccessToken)
                .append("]").toString();
    }
}