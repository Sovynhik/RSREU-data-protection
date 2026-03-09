package ru.rsreu.sovynhik.pract_03.service;

import ru.rsreu.sovynhik.pract_03.model.Right;
import ru.rsreu.sovynhik.pract_03.model.User;

public class AuthorizationService {
    private final AccessMatrixService accessMatrixService;

    public AuthorizationService(AccessMatrixService accessMatrixService) {
        this.accessMatrixService = accessMatrixService;
    }

    public boolean canRead(User user, String objectName) {
        return accessMatrixService.checkAccess(user, objectName, Right.READ);
    }

    public boolean canWrite(User user, String objectName) {
        return accessMatrixService.checkAccess(user, objectName, Right.WRITE);
    }

    public boolean canGrant(User user, String objectName) {
        return accessMatrixService.checkAccess(user, objectName, Right.GRANT);
    }
}