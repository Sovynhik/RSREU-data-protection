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
        rights.add(Right.GRANT); // обязательно для всех обычных пользователей
        if (random.nextBoolean()) {
            rights.add(Right.READ);
        }
        if (random.nextBoolean()) {
            rights.add(Right.WRITE);
        }
        return rights;
    }

    public boolean checkAccess(User user, String objectName, Right requiredRight) {
        Optional<SystemObject> object = objectRepository.findByName(objectName);
        return object.filter(o -> accessMatrixRepository.hasRight(user, o, requiredRight)).isPresent();
    }

    /**
     * Проверяет, приведёт ли передача права к тому, что целевой пользователь
     * получит полные права (READ, WRITE, GRANT) на все объекты.
     */
    public boolean wouldTransferMakeAdmin(User toUser, String objectName, Right right) {
        Optional<SystemObject> obj = objectRepository.findByName(objectName);
        if (obj.isEmpty()) return false;
        return wouldBecomeAdmin(toUser, obj.get(), right);
    }

    private boolean wouldBecomeAdmin(User target, SystemObject targetObject, Right newRight) {
        if (target.isAdmin()) return false; // админ уже есть, но это не наш случай

        // Проверяем, есть ли у target уже все права на всех объектах, кроме целевого
        for (SystemObject obj : objectRepository.findAll()) {
            if (obj.equals(targetObject)) continue;
            if (!hasAllRights(target, obj)) {
                return false; // на каком-то другом объекте не все права, значит не станет админом
            }
        }

        // Проверяем, будут ли у target все права на целевом объекте после добавления
        Set<Right> currentRights = accessMatrixRepository.getRights(target, targetObject);
        Set<Right> afterRights = new HashSet<>(currentRights);
        afterRights.add(newRight);
        return afterRights.containsAll(List.of(Right.READ, Right.WRITE, Right.GRANT));
    }

    private boolean hasAllRights(User user, SystemObject object) {
        Set<Right> rights = accessMatrixRepository.getRights(user, object);
        return rights.containsAll(List.of(Right.READ, Right.WRITE, Right.GRANT));
    }

    public boolean transferRight(User from, String objectName, Right right, String toUserName) {
        if (right == null || right.isDenied()) return false;
        Optional<SystemObject> object = objectRepository.findByName(objectName);
        Optional<User> toUser = userRepository.findByName(toUserName);
        if (object.isEmpty() || toUser.isEmpty()) return false;

        // Проверка права на передачу у from
        if (!from.isAdmin() && !accessMatrixRepository.hasRight(from, object.get(), Right.GRANT))
            return false;
        if (!from.isAdmin() && !accessMatrixRepository.hasRight(from, object.get(), right))
            return false;

        // Проверка, не станет ли получатель администратором
        if (wouldBecomeAdmin(toUser.get(), object.get(), right)) {
            return false;
        }

        // Если у получателя уже есть это право, ничего не делаем
        if (accessMatrixRepository.hasRight(toUser.get(), object.get(), right)) {
            return false;
        }

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

    public List<SystemObject> getAllObjects() {
        return objectRepository.findAll();
    }

    public Map<User, Map<SystemObject, Set<Right>>> getAllRightsMatrix() {
        Map<User, Map<SystemObject, Set<Right>>> matrix = new LinkedHashMap<>();
        for (User user : userRepository.findAll()) {
            Map<SystemObject, Set<Right>> userRights = new LinkedHashMap<>();
            for (SystemObject object : objectRepository.findAll()) {
                userRights.put(object, accessMatrixRepository.getRights(user, object));
            }
            matrix.put(user, userRights);
        }
        return matrix;
    }
}