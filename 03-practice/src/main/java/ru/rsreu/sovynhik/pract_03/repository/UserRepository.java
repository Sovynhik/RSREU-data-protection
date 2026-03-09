package ru.rsreu.sovynhik.pract_03.repository;

import ru.rsreu.sovynhik.pract_03.model.User;
import java.util.*;

/**
 * Репозиторий для хранения и управления пользователями системы.
 * Обеспечивает поиск пользователей по имени (без учета регистра) и получение списка всех пользователей.
 */
public class UserRepository {
    private final List<User> users = new ArrayList<>();
    private final Map<String, User> userMap = new HashMap<>();

    /**
     * Добавляет пользователя в репозиторий.
     * Пользователь сохраняется в списке и в карте для быстрого поиска по имени (в нижнем регистре).
     *
     * @param user добавляемый пользователь
     */
    public void addUser(User user) {
        users.add(user);
        userMap.put(user.getName().toLowerCase(), user);
    }

    /**
     * Выполняет поиск пользователя по его имени.
     * Поиск выполняется без учета регистра.
     *
     * @param name имя пользователя для поиска
     * @return {@link Optional}, содержащий найденного пользователя, или пустой {@link Optional}, если пользователь не найден
     */
    public Optional<User> findByName(String name) {
        if (name == null) return Optional.empty();
        return Optional.ofNullable(userMap.get(name.toLowerCase()));
    }

    /**
     * Возвращает неизменяемый список всех пользователей.
     *
     * @return неизменяемый список пользователей
     */
    public List<User> findAll() {
        return Collections.unmodifiableList(users);
    }

    /**
     * Возвращает количество пользователей в репозитории.
     *
     * @return количество пользователей
     */
    public int getCount() {
        return users.size();
    }
}