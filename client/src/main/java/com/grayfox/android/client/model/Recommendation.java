package com.grayfox.android.client.model;

import java.io.Serializable;
import java.util.Arrays;

public class Recommendation implements Serializable {

    public static enum Type {SOCIAL, SELF}

    private static final long serialVersionUID = 4322497520093419157L;

    private Type type;
    private String reason;
    private Poi[] poiSequence;
    private Location[] routePoints;

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

    public Poi[] getPoiSequence() {
        return poiSequence;
    }

    public void setPoiSequence(Poi[] poiSequence) {
        this.poiSequence = poiSequence;
    }

    public Location[] getRoutePoints() {
        return routePoints;
    }

    public void setRoutePoints(Location[] routePoints) {
        this.routePoints = routePoints;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((reason == null) ? 0 : reason.hashCode());
        result = prime * result + Arrays.hashCode(poiSequence);
        result = prime * result + Arrays.hashCode(routePoints);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Recommendation other = (Recommendation) obj;
        if (!Arrays.equals(poiSequence, other.poiSequence)) return false;
        if (!Arrays.equals(routePoints, other.routePoints)) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Recommendation [poiSequence=").append(Arrays.toString(poiSequence)).append(", routePoints=").append(Arrays.toString(routePoints)).append("]");
        return builder.toString();
    }
}