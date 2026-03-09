package ru.rsreu.sovynhik.pract_03.model;

import java.util.Objects;

public class AccessEntry {
    private final String userName;
    private final String objectName;

    public AccessEntry(String userName, String objectName) {
        this.userName = userName;
        this.objectName = objectName;
    }

    public AccessEntry(User user, SystemObject object) {
        this(user.getName(), object.getName());
    }

    public String getUserName() {
        return userName;
    }

    public String getObjectName() {
        return objectName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessEntry that = (AccessEntry) o;
        return Objects.equals(userName, that.userName) &&
                Objects.equals(objectName, that.objectName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, objectName);
    }

    @Override
    public String toString() {
        return userName + ":" + objectName;
    }
}