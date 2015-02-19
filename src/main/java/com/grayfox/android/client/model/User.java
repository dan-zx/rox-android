package com.grayfox.android.client.model;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 8194213994981653663L;

    private String name;
    private String lastName;
    private String photoUrl;
    private String foursquareId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getFoursquareId() {
        return foursquareId;
    }

    public void setFoursquareId(String foursquareId) {
        this.foursquareId = foursquareId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((foursquareId == null) ? 0 : foursquareId.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((photoUrl == null) ? 0 : photoUrl.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        User other = (User) obj;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        if (foursquareId == null) {
            if (other.foursquareId != null) return false;
        } else if (!foursquareId.equals(other.foursquareId)) return false;
        if (lastName == null) {
            if (other.lastName != null) return false;
        } else if (!lastName.equals(other.lastName)) return false;
        if (photoUrl == null) {
            if (other.photoUrl != null) return false;
        } else if (!photoUrl.equals(other.photoUrl)) return false;
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("User [foursquareId=").append(foursquareId).append(", name=")
                .append(name).append(", lastName=").append(lastName).append(", photoUrl=")
                .append(photoUrl).append("]").toString();
    }
}
