package com.grayfox.android.client.model;

import java.io.Serializable;

public class Recommendation implements Serializable {

    public static enum Type {GLOBAL, SELF, SOCIAL}

    private static final long serialVersionUID = 4322497520093419157L;

    private Type type;
    private String reason;
    private Poi poi;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Poi getPoi() {
        return poi;
    }

    public void setPoi(Poi poi) {
        this.poi = poi;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((poi == null) ? 0 : poi.hashCode());
        result = prime * result + ((reason == null) ? 0 : reason.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Recommendation other = (Recommendation) obj;
        if (poi == null) {
            if (other.poi != null) return false;
        } else if (!poi.equals(other.poi)) return false;
        if (reason == null) {
            if (other.reason != null) return false;
        } else if (!reason.equals(other.reason)) return false;
        if (type != other.type) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Recommendation [type=").append(type).append(", reason=").append(reason).append(", poi=").append(poi).append("]");
        return builder.toString();
    }
}