package ru.rsreu.sovynhik.pract_03.service;

import ru.rsreu.sovynhik.pract_03.model.Right;
import ru.rsreu.sovynhik.pract_03.model.User;

/**
 * Сервис авторизации, проверяющий наличие у пользователя конкретных прав на объекты.
 * Делегирует проверки в {@link AccessMatrixService}.
 */
public class AuthorizationService {
    private final AccessMatrixService accessMatrixService;

    /**
     * Создает сервис авторизации с указанным сервисом матрицы доступа.
     *
     * @param accessMatrixService сервис для проверки прав в матрице доступа
     */
    public AuthorizationService(AccessMatrixService accessMatrixService) {
        this.accessMatrixService = accessMatrixService;
    }

    /**
     * Проверяет, имеет ли пользователь право на чтение указанного объекта.
     *
     * @param user       пользователь
     * @param objectName имя объекта
     * @return true если пользователь имеет право на чтение, иначе false
     */
    public boolean canRead(User user, String objectName) {
        return accessMatrixService.checkAccess(user, objectName, Right.READ);
    }

    /**
     * Проверяет, имеет ли пользователь право на запись в указанный объект.
     *
     * @param user       пользователь
     * @param objectName имя объекта
     * @return true если пользователь имеет право на запись, иначе false
     */
    public boolean canWrite(User user, String objectName) {
        return accessMatrixService.checkAccess(user, objectName, Right.WRITE);
    }

    /**
     * Проверяет, имеет ли пользователь право на передачу прав на указанный объект.
     *
     * @param user       пользователь
     * @param objectName имя объекта
     * @return true если пользователь имеет право на передачу прав, иначе false
     */
    public boolean canGrant(User user, String objectName) {
        return accessMatrixService.checkAccess(user, objectName, Right.GRANT);
    }
}