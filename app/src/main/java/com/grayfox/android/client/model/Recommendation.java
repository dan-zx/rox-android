package com.grayfox.android.client.model;

import java.io.Serializable;
import java.util.List;

public class Recommendation implements Serializable {

    private static final long serialVersionUID = 4322497520093419157L;

    private List<Poi> pois;
    private List<Location> routePoints;

    public List<Poi> getPois() {
        return pois;
    }

    public void setPois(List<Poi> pois) {
        this.pois = pois;
    }

    public List<Location> getRoutePoints() {
        return routePoints;
    }

    public void setRoutePoints(List<Location> routePoints) {
        this.routePoints = routePoints;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pois == null) ? 0 : pois.hashCode());
        result = prime * result + ((routePoints == null) ? 0 : routePoints.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Recommendation other = (Recommendation) obj;
        if (pois == null) {
            if (other.pois != null) return false;
        } else if (!pois.equals(other.pois)) return false;
        if (routePoints == null) {
            if (other.routePoints != null) return false;
        } else if (!routePoints.equals(other.routePoints)) return false;
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("Recommendation [pois=").append(pois)
                .append(", routePoints=").append(routePoints).append("]").toString();
    }
}