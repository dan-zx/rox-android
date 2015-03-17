package com.grayfox.android.client.model;

import java.io.Serializable;

public class UpdateResult implements Serializable {

    private static final long serialVersionUID = 1450022070052339450L;

    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (success ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        UpdateResult other = (UpdateResult) obj;
        if (success != other.success) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UpdateResult [success=").append(success).append("]");
        return builder.toString();
    }
    
}
