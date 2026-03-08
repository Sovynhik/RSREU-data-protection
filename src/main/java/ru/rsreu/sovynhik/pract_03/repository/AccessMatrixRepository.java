package ru.rsreu.sovynhik.pract_03.repository;

import ru.rsreu.sovynhik.pract_03.model.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AccessMatrixRepository {
    private final Map<AccessEntry, Set<Right>> matrix = new ConcurrentHashMap<>();

    public Set<Right> getRights(User user, SystemObject object) {
        AccessEntry key = new AccessEntry(user, object);
        return Collections.unmodifiableSet(
                matrix.getOrDefault(key, new HashSet<>())
        );
    }

    public void setRights(User user, SystemObject object, Set<Right> rights) {
        AccessEntry key = new AccessEntry(user, object);
        matrix.put(key, new HashSet<>(rights));
    }

    public void addRight(User user, SystemObject object, Right right) {
        if (right == null || right.isDenied()) return;
        AccessEntry key = new AccessEntry(user, object);
        matrix.computeIfAbsent(key, k -> new HashSet<>()).add(right);
        matrix.get(key).remove(Right.DENIED);
    }

    public boolean hasRight(User user, SystemObject object, Right right) {
        if (user.isAdmin()) return true;
        AccessEntry key = new AccessEntry(user, object);
        Set<Right> rights = matrix.get(key);
        return rights != null && rights.contains(right);
    }

    public void clear() {
        matrix.clear();
    }
}