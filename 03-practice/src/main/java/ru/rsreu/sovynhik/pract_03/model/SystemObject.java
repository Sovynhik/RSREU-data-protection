package ru.rsreu.sovynhik.pract_03.model;

import ru.rsreu.sovynhik.pract_03.config.Constants;

/**
 * Представляет системный объект (ресурс), к которому может быть ограничен доступ.
 * Каждый объект имеет уникальный идентификатор и отображаемое имя.
 */
public class SystemObject {
    private final String id;
    private final String name;

    /**
     * Создает системный объект с заданным индексом.
     * Идентификатор формируется как "OBJ_" + индекс, а отображаемое имя берется из конфигурации.
     *
     * @param index уникальный индекс объекта
     */
    public SystemObject(int index) {
        this.id = "OBJ_" + index;
        this.name = Constants.OBJECT_PREFIX + index;
    }

    /**
     * Возвращает отображаемое имя объекта.
     *
     * @return имя объекта
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает строковое представление объекта (его имя).
     *
     * @return имя объекта
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Сравнивает этот объект с указанным объектом на равенство.
     * Два системных объекта считаются равными, если их имена совпадают.
     *
     * @param o объект для сравнения
     * @return true если объекты равны, иначе false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemObject that = (SystemObject) o;
        return name.equals(that.name);
    }

    /**
     * Возвращает хеш-код объекта, вычисленный на основе его имени.
     *
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}