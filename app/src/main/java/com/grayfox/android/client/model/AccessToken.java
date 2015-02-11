package com.grayfox.android.client.model;

import java.io.Serializable;

public class AccessToken implements Serializable {

    private static final long serialVersionUID = 564037530412089830L;

    private String accessToken;

    public String getToken() {
        return accessToken;
    }

    public void setToken(String appAccessToken) {
        this.accessToken = appAccessToken;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accessToken == null) ? 0 : accessToken.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        AccessToken other = (AccessToken) obj;
        if (accessToken == null) {
            if (other.accessToken != null) return false;
        } else if (!accessToken.equals(other.accessToken)) return false;
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("AccessToken [accessToken=").append(accessToken)
                .append("]").toString();
    }
}