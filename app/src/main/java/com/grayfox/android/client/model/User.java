package com.grayfox.android.client.model;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 8194213994981653663L;

    private String id;
    private String firstName;
    private String lastName;
    private String photoUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        if (firstName == null) {
            if (other.firstName != null) return false;
        } else if (!firstName.equals(other.firstName)) return false;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
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
        return new StringBuilder().append("UserResponse [id=").append(id).append(", firstName=")
                .append(firstName).append(", lastName=").append(lastName).append(", photoUrl=")
                .append(photoUrl).append("]").toString();
    }
}
