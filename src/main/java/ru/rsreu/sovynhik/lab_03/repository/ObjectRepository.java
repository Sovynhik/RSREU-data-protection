package ru.rsreu.sovynhik.lab_03.repository;

import ru.rsreu.sovynhik.lab_03.model.SystemObject;
import java.util.*;

public class ObjectRepository {
    private final List<SystemObject> objects = new ArrayList<>();
    private final Map<String, SystemObject> objectMap = new HashMap<>();

    public void addObject(SystemObject object) {
        objects.add(object);
        objectMap.put(object.getName().toLowerCase(), object);
    }

    public Optional<SystemObject> findByName(String name) {
        if (name == null) return Optional.empty();
        return Optional.ofNullable(objectMap.get(name.toLowerCase()));
    }

    public List<SystemObject> findAll() {
        return Collections.unmodifiableList(objects);
    }

    public int getCount() {
        return objects.size();
    }

    public boolean exists(String name) {
        return objectMap.containsKey(name.toLowerCase());
    }
}