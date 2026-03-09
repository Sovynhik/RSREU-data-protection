package ru.rsreu.sovynhik.pract_03.model;

import java.util.Objects;

/**
 * Представляет составной ключ для записи в матрице доступа,
 * однозначно идентифицируемый именем пользователя и именем объекта.
 * Используется в качестве ключа в {@link java.util.Map} для хранения прав доступа.
 */
public class AccessEntry {
    private final String userName;
    private final String objectName;

    /**
     * Создает запись доступа по именам пользователя и объекта.
     *
     * @param userName   имя пользователя
     * @param objectName имя объекта
     */
    public AccessEntry(String userName, String objectName) {
        this.userName = userName;
        this.objectName = objectName;
    }

    /**
     * Создает запись доступа на основе объектов пользователя и системного объекта.
     *
     * @param user   пользователь
     * @param object системный объект
     */
    public AccessEntry(User user, SystemObject object) {
        this(user.getName(), object.getName());
    }

    /**
     * Сравнивает эту запись с указанным объектом на равенство.
     * Две записи считаются равными, если имена пользователей и объектов совпадают.
     *
     * @param o объект для сравнения
     * @return true если объекты равны, иначе false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessEntry that = (AccessEntry) o;
        return Objects.equals(userName, that.userName) &&
                Objects.equals(objectName, that.objectName);
    }

    /**
     * Возвращает хеш-код записи, вычисленный на основе имен пользователя и объекта.
     *
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        return Objects.hash(userName, objectName);
    }

    /**
     * Возвращает строковое представление записи в формате "имя_пользователя:имя_объекта".
     *
     * @return строковое представление
     */
    @Override
    public String toString() {
        return userName + ":" + objectName;
    }
}