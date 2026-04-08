package ru.rsreu.sovynhik.pract_03.repository;

import ru.rsreu.sovynhik.pract_03.model.SystemObject;
import java.util.*;

/**
 * Репозиторий для хранения и управления системными объектами.
 * Обеспечивает поиск объектов по имени (без учета регистра) и получение списка всех объектов.
 */
public class ObjectRepository {
    private final List<SystemObject> objects = new ArrayList<>();
    private final Map<String, SystemObject> objectMap = new HashMap<>();

    /**
     * Добавляет системный объект в репозиторий.
     * Объект сохраняется в списке и в карте для быстрого поиска по имени (в нижнем регистре).
     *
     * @param object добавляемый системный объект
     */
    public void addObject(SystemObject object) {
        objects.add(object);
        objectMap.put(object.getName().toLowerCase(), object);
    }

    /**
     * Выполняет поиск системного объекта по его имени.
     * Поиск выполняется без учета регистра.
     *
     * @param name имя объекта для поиска
     * @return {@link Optional}, содержащий найденный объект, или пустой {@link Optional}, если объект не найден
     */
    public Optional<SystemObject> findByName(String name) {
        if (name == null) return Optional.empty();
        return Optional.ofNullable(objectMap.get(name.toLowerCase()));
    }

    /**
     * Возвращает неизменяемый список всех системных объектов.
     *
     * @return неизменяемый список объектов
     */
    public List<SystemObject> findAll() {
        return Collections.unmodifiableList(objects);
    }

    /**
     * Возвращает количество объектов в репозитории.
     *
     * @return количество объектов
     */
    public int getCount() {
        return objects.size();
    }
}