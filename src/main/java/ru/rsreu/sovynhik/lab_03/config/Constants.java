package ru.rsreu.sovynhik.lab_03.config;

public final class Constants {
    private Constants() {}

    // Конфигурация системы
    public static final int USER_COUNT = 3;
    public static final int OBJECT_COUNT = 5;
    public static final int MAX_ACCESS_LEVEL = 4;

    // Имена пользователей
    public static final String ADMIN_NAME = "Admin";
    public static final String USER_PREFIX = "User";

    // Имена объектов
    public static final String OBJECT_PREFIX = "Объект_";

    // Права доступа
    public static final String RIGHT_READ = "Чтение";
    public static final String RIGHT_WRITE = "Запись";
    public static final String RIGHT_GRANT = "Передача прав";
    public static final String RIGHT_DENIED = "Запрет";

    // Алиасы команд (для ввода)
    public static final String CMD_READ = "read";
    public static final String CMD_WRITE = "write";
    public static final String CMD_GRANT = "grant";
    public static final String CMD_QUIT = "quit";

    // Алиасы прав (для ввода)
    public static final String ALIAS_READ_RU = "Чтение";
    public static final String ALIAS_READ_EN = "read";
    public static final String ALIAS_WRITE_RU = "Запись";
    public static final String ALIAS_WRITE_EN = "write";
    public static final String ALIAS_GRANT_RU = "Передача прав";
    public static final String ALIAS_GRANT_EN = "grant";

    // Сообщения пользователю
    public static final String MSG_WELCOME = "=== Система дискреционного доступа (DAC) ===";
    public static final String MSG_STATS = "Пользователей: %d, Объектов: %d";
    public static final String MSG_SEPARATOR = "===========================================";
    public static final String MSG_ENTER_USER = "\nВведите идентификатор пользователя: ";
    public static final String MSG_AUTH_SUCCESS = "\nИдентификация прошла успешно, добро пожаловать в систему, %s! %s";
    public static final String MSG_ADMIN_SUFFIX = "(Администратор)";
    public static final String MSG_AUTH_FAIL = "Неуспешная идентификация пользователя. Попробуйте еще раз.";
    public static final String MSG_YOUR_RIGHTS = "\nПеречень Ваших прав:";
    public static final String MSG_RIGHTS_FORMAT = "  %s: %s";
    public static final String MSG_PROMPT = "\nЖду ваших указаний > ";
    public static final String MSG_GOODBYE = "Работа пользователя %s завершена. До свидания.";
    public static final String MSG_UNKNOWN_COMMAND = "Неизвестная команда. Попробуйте: read/write/grant/quit";
    public static final String MSG_INVALID_FORMAT = "Неверный формат. Используйте: %s";
    public static final String MSG_OBJECT_NOT_FOUND = "Объект не найден.";
    public static final String MSG_USER_NOT_FOUND = "Пользователь не найден.";
    public static final String MSG_RIGHT_NOT_FOUND = "Неизвестное право. Доступно: Чтение, Запись, Передача прав";
    public static final String MSG_NO_GRANT_RIGHT = "Отказ в выполнении операции. У Вас нет права 'Передача прав' для этого объекта.";
    public static final String MSG_NO_RIGHT_TO_TRANSFER = "Отказ в выполнении операции. Вы не можете передать право, которым не обладаете.";
    public static final String MSG_ACCESS_DENIED = "Отказ в выполнении операции. У Вас нет прав для ее осуществления.";
    public static final String MSG_ACCESS_GRANTED = "Операция прошла успешно.";
    public static final String MSG_GRANT_SUCCESS = "Операция прошла успешно. Право '%s' на объект %s передано пользователю %s.";
    public static final String MSG_NO_RIGHTS = "Нет прав";
    public static final String MSG_SYSTEM_INIT = "Система инициализирована. Пользователей: %d, Объектов: %d";
    public static final String MSG_ENTER_COMMAND = "Введите команду";

    // Форматы
    public static final String GRANT_FORMAT = "grant объект право пользователь";
}