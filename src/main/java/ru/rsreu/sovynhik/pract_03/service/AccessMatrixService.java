package ru.rsreu.sovynhik.pract_03.service;

import ru.rsreu.sovynhik.pract_03.config.Constants;
import ru.rsreu.sovynhik.pract_03.model.*;
import ru.rsreu.sovynhik.pract_03.repository.*;
import java.util.*;

public class AccessMatrixService {
    private final AccessMatrixRepository accessMatrixRepository;
    private final UserRepository userRepository;
    private final ObjectRepository objectRepository;
    private final Random random = new Random();

    public AccessMatrixService(AccessMatrixRepository accessMatrixRepository,
                               UserRepository userRepository,
                               ObjectRepository objectRepository) {
        this.accessMatrixRepository = accessMatrixRepository;
        this.userRepository = userRepository;
        this.objectRepository = objectRepository;
    }

    public void initializeMatrix() {
        accessMatrixRepository.clear();

        for (User user : userRepository.findAll()) {
            for (SystemObject object : objectRepository.findAll()) {
                Set<Right> rights = generateRightsForUser(user);
                accessMatrixRepository.setRights(user, object, rights);
            }
        }

        System.out.printf(Constants.MSG_SYSTEM_INIT + "\n",
                userRepository.getCount(), objectRepository.getCount());
    }

    private Set<Right> generateRightsForUser(User user) {
        if (user.isAdmin()) {
            return new HashSet<>(Arrays.asList(Right.READ, Right.WRITE, Right.GRANT));
        }

        Set<Right> rights = new HashSet<>();
        int accessLevel = random.nextInt(Constants.MAX_ACCESS_LEVEL);

        switch (accessLevel) {
            case 0:
                rights.add(Right.DENIED);
                break;
            case 1:
                rights.add(Right.READ);
                break;
            case 2:
                rights.add(Right.WRITE);
                break;
            case 3:
                rights.addAll(Arrays.asList(Right.READ, Right.WRITE));
                break;
            default:
                rights.add(Right.DENIED);
        }
        return rights;
    }

    public boolean checkAccess(User user, String objectName, Right requiredRight) {
        Optional<SystemObject> object = objectRepository.findByName(objectName);
        if (object.isEmpty()) return false;

        return accessMatrixRepository.hasRight(user, object.get(), requiredRight);
    }

    public boolean transferRight(User from, String objectName, Right right, String toUserName) {
        if (right == null || right.isDenied()) return false;

        Optional<SystemObject> object = objectRepository.findByName(objectName);
        Optional<User> toUser = userRepository.findByName(toUserName);

        if (object.isEmpty() || toUser.isEmpty()) {
            return false;
        }

        // Проверяем право на передачу
        if (!from.isAdmin() &&
                !accessMatrixRepository.hasRight(from, object.get(), Right.GRANT)) {
            return false;
        }

        // Проверяем наличие передаваемого права
        if (!from.isAdmin() &&
                !accessMatrixRepository.hasRight(from, object.get(), right)) {
            return false;
        }

        // Передаем право
        accessMatrixRepository.addRight(toUser.get(), object.get(), right);
        return true;
    }

    public Map<SystemObject, Set<Right>> getUserRights(User user) {
        Map<SystemObject, Set<Right>> result = new LinkedHashMap<>();
        for (SystemObject object : objectRepository.findAll()) {
            result.put(object, accessMatrixRepository.getRights(user, object));
        }
        return result;
    }

    public Optional<SystemObject> findObject(String name) {
        return objectRepository.findByName(name);
    }

    public Optional<User> findUser(String name) {
        return userRepository.findByName(name);
    }
}