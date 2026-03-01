package ru.rsreu.sovynhik.lab_03.model;

import ru.rsreu.sovynhik.lab_03.config.Constants;

public class User {
    private final String name;
    private final boolean isAdmin;

    public User(String name, boolean isAdmin) {
        this.name = name;
        this.isAdmin = isAdmin;
    }

    public String getName() { return name; }
    public boolean isAdmin() { return isAdmin; }

    @Override
    public String toString() {
        return name + (isAdmin ? " " + Constants.MSG_ADMIN_SUFFIX : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return name.equalsIgnoreCase(user.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }
}