package ru.rsreu.sovynhik.pract_03.repository;

import ru.rsreu.sovynhik.pract_03.model.User;
import java.util.*;

public class UserRepository {
    private final List<User> users = new ArrayList<>();
    private final Map<String, User> userMap = new HashMap<>();

    public void addUser(User user) {
        users.add(user);
        userMap.put(user.getName().toLowerCase(), user);
    }

    public Optional<User> findByName(String name) {
        if (name == null) return Optional.empty();
        return Optional.ofNullable(userMap.get(name.toLowerCase()));
    }

    public List<User> findAll() {
        return Collections.unmodifiableList(users);
    }

    public int getCount() {
        return users.size();
    }

    public boolean exists(String name) {
        return userMap.containsKey(name.toLowerCase());
    }
}