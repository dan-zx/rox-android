package com.grayfox.android.client.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Recommendation implements Serializable {

    private static final long serialVersionUID = 4322497520093419157L;

    private Poi[] pois;
    private Location[] route;

    public Poi[] getPois() {
        return pois;
    }

    public void setPois(Poi[] pois) {
        this.pois = pois;
    }

    public Location[] getRoute() {
        return route;
    }

    public void setRoute(Location[] route) {
        this.route = route;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(pois);
        result = prime * result + Arrays.hashCode(route);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Recommendation other = (Recommendation) obj;
        if (!Arrays.equals(pois, other.pois)) return false;
        if (!Arrays.equals(route, other.route)) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Recommendation [pois=").append(Arrays.toString(pois)).append(", route=").append(Arrays.toString(route)).append("]");
        return builder.toString();
    }
}