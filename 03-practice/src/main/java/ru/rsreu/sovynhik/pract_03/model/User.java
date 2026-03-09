package ru.rsreu.sovynhik.pract_03.model;

import ru.rsreu.sovynhik.pract_03.config.Constants;

/**
 * Представляет пользователя системы дискреционного управления доступом (DAC).
 * Пользователь может быть обычным или администратором (имеет все права на все объекты).
 * Сравнение пользователей выполняется по имени без учета регистра.
 */
public class User {
    private final String name;
    private final boolean isAdmin;

    /**
     * Создает пользователя с указанным именем и флагом администратора.
     *
     * @param name    имя пользователя
     * @param isAdmin true, если пользователь является администратором, иначе false
     */
    public User(String name, boolean isAdmin) {
        this.name = name;
        this.isAdmin = isAdmin;
    }

    /**
     * Возвращает имя пользователя.
     *
     * @return имя пользователя
     */
    public String getName() {
        return name;
    }

    /**
     * Проверяет, является ли пользователь администратором.
     *
     * @return true, если пользователь администратор, иначе false
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * Возвращает строковое представление пользователя.
     * Для администратора к имени добавляется суффикс из конфигурации.
     *
     * @return строковое представление пользователя
     */
    @Override
    public String toString() {
        return name + (isAdmin ? " " + Constants.MSG_ADMIN_SUFFIX : "");
    }

    /**
     * Сравнивает этого пользователя с указанным объектом на равенство.
     * Два пользователя считаются равными, если их имена совпадают без учета регистра.
     *
     * @param o объект для сравнения
     * @return true если пользователи равны, иначе false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return name.equalsIgnoreCase(user.name);
    }

    /**
     * Возвращает хеш-код пользователя, вычисленный на основе его имени в нижнем регистре.
     *
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }
}