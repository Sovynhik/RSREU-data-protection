package ru.rsreu.sovynhik.pract_03.repository;

import ru.rsreu.sovynhik.pract_03.model.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Репозиторий для хранения и управления матрицей доступа (дискреционная модель).
 * Реализует потокобезопасное хранение прав доступа для пар (пользователь, объект)
 * с использованием {@link ConcurrentHashMap}.
 */
public class AccessMatrixRepository {
    private final Map<AccessEntry, Set<Right>> matrix = new ConcurrentHashMap<>();

    /**
     * Возвращает неизменяемое множество прав доступа для указанного пользователя и объекта.
     * Если права не найдены, возвращается пустое множество.
     *
     * @param user   пользователь
     * @param object системный объект
     * @return неизменяемое множество прав доступа
     */
    public Set<Right> getRights(User user, SystemObject object) {
        AccessEntry key = new AccessEntry(user, object);
        return Collections.unmodifiableSet(
                matrix.getOrDefault(key, new HashSet<>())
        );
    }

    /**
     * Устанавливает права доступа для указанного пользователя и объекта.
     * Заменяет существующие права новым множеством.
     *
     * @param user   пользователь
     * @param object системный объект
     * @param rights множество прав доступа (будет скопировано)
     */
    public void setRights(User user, SystemObject object, Set<Right> rights) {
        AccessEntry key = new AccessEntry(user, object);
        matrix.put(key, new HashSet<>(rights));
    }

    /**
     * Добавляет право доступа для указанного пользователя и объекта.
     * Если право равно {@code null} или является {@link Right#DENIED}, операция игнорируется.
     * При добавлении любого права автоматически удаляется {@link Right#DENIED} для данной записи.
     *
     * @param user   пользователь
     * @param object системный объект
     * @param right  добавляемое право
     */
    public void addRight(User user, SystemObject object, Right right) {
        if (right == null || right.isDenied()) return;
        AccessEntry key = new AccessEntry(user, object);
        matrix.computeIfAbsent(key, k -> new HashSet<>()).add(right);
        matrix.get(key).remove(Right.DENIED);
    }

    /**
     * Проверяет наличие указанного права доступа у пользователя на объект.
     * Администратор всегда имеет все права (возвращает {@code true}).
     *
     * @param user   пользователь
     * @param object системный объект
     * @param right  проверяемое право
     * @return {@code true}, если пользователь имеет право, иначе {@code false}
     */
    public boolean hasRight(User user, SystemObject object, Right right) {
        if (user.isAdmin()) return true;
        AccessEntry key = new AccessEntry(user, object);
        Set<Right> rights = matrix.get(key);
        return rights != null && rights.contains(right);
    }

    /**
     * Очищает всю матрицу доступа, удаляя все записи о правах.
     */
    public void clear() {
        matrix.clear();
    }
}