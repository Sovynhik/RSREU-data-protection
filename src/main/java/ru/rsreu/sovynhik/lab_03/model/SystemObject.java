package ru.rsreu.sovynhik.lab_03.model;

import ru.rsreu.sovynhik.lab_03.config.Constants;

public class SystemObject {
    private final String id;
    private final String name;

    public SystemObject(int index) {
        this.id = "OBJ_" + index;
        this.name = Constants.OBJECT_PREFIX + index;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemObject that = (SystemObject) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}