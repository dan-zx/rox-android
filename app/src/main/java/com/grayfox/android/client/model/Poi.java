package com.grayfox.android.client.model;

import java.io.Serializable;

public class Poi implements Serializable {

    private static final long serialVersionUID = 7105577912216669366L;

    private String id;
    private String name;
    private Location location;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Poi other = (Poi) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        if (location == null) {
            if (other.location != null) return false;
        } else if (!location.equals(other.location)) return false;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("Poi [id=").append(id).append(", name=").append(name)
                .append(", location=").append(location).append("]").toString();
    }
}