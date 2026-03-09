package ru.rsreu.sovynhik.pract_03.model;

import ru.rsreu.sovynhik.pract_03.config.Constants;

/**
 * Перечисление возможных прав доступа в системе дискреционного управления доступом (DAC).
 * Каждое право имеет отображаемое имя и набор псевдонимов для гибкого парсинга пользовательского ввода.
 */
public enum Right {
    /**
     * Право на чтение объекта.
     */
    READ(Constants.RIGHT_READ, Constants.ALIAS_READ_RU, Constants.ALIAS_READ_EN),

    /**
     * Право на запись (модификацию) объекта.
     */
    WRITE(Constants.RIGHT_WRITE, Constants.ALIAS_WRITE_RU, Constants.ALIAS_WRITE_EN),

    /**
     * Право на передачу прав другим пользователям.
     */
    GRANT(Constants.RIGHT_GRANT, Constants.ALIAS_GRANT_RU, Constants.ALIAS_GRANT_EN),

    /**
     * Специальное право, обозначающее отсутствие доступа.
     */
    DENIED(Constants.RIGHT_DENIED, Constants.RIGHT_DENIED);

    private final String displayName;
    private final String[] aliases;

    /**
     * Конструктор права доступа.
     *
     * @param displayName отображаемое имя права
     * @param aliases     набор псевдонимов для распознавания ввода (могут включать как русские, так и английские варианты)
     */
    Right(String displayName, String... aliases) {
        this.displayName = displayName;
        this.aliases = aliases;
    }

    /**
     * Возвращает отображаемое имя права.
     *
     * @return отображаемое имя
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Преобразует строковое представление права (имя или псевдоним) в соответствующее значение перечисления.
     * Поиск выполняется без учета регистра.
     *
     * @param text строковое представление права (может быть отображаемым именем или псевдонимом)
     * @return соответствующее значение {@code Right}, или {@code null}, если соответствие не найдено
     */
    public static Right fromString(String text) {
        if (text == null) return null;

        for (Right right : Right.values()) {
            if (right.displayName.equalsIgnoreCase(text)) {
                return right;
            }
            for (String alias : right.aliases) {
                if (alias.equalsIgnoreCase(text)) {
                    return right;
                }
            }
        }
        return null;
    }

    /**
     * Проверяет, является ли данное право специальным значением "Запрет".
     *
     * @return {@code true}, если право равно {@link #DENIED}, иначе {@code false}
     */
    public boolean isDenied() {
        return this == DENIED;
    }
}