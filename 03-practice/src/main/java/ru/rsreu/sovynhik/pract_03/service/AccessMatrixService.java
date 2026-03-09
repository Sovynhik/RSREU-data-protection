package ru.rsreu.sovynhik.pract_03.service;

import ru.rsreu.sovynhik.pract_03.config.Constants;
import ru.rsreu.sovynhik.pract_03.model.*;
import ru.rsreu.sovynhik.pract_03.repository.*;
import java.util.*;

/**
 * Сервис для управления матрицей доступа и выполнения операций с правами.
 * Реализует бизнес-логику дискреционного управления доступом (DAC):
 * - инициализация матрицы прав
 * - проверка доступа
 * - передача прав между пользователями
 * - получение информации о правах
 */
public class AccessMatrixService {
    private final AccessMatrixRepository accessMatrixRepository;
    private final UserRepository userRepository;
    private final ObjectRepository objectRepository;
    private final Random random = new Random();

    /**
     * Создает сервис с указанными репозиториями.
     *
     * @param accessMatrixRepository репозиторий матрицы доступа
     * @param userRepository         репозиторий пользователей
     * @param objectRepository       репозиторий объектов
     */
    public AccessMatrixService(AccessMatrixRepository accessMatrixRepository,
                               UserRepository userRepository,
                               ObjectRepository objectRepository) {
        this.accessMatrixRepository = accessMatrixRepository;
        this.userRepository = userRepository;
        this.objectRepository = objectRepository;
    }

    /**
     * Инициализирует матрицу доступа, заполняя её случайными правами для всех пар (пользователь, объект).
     * Администраторы получают полный набор прав (READ, WRITE, GRANT).
     * Обычные пользователи всегда получают право GRANT, а READ и WRITE добавляются случайным образом.
     * После инициализации выводится информация о количестве пользователей и объектов.
     */
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

    /**
     * Генерирует набор прав для пользователя.
     * Администратор получает все права, обычный пользователь всегда имеет GRANT,
     * а READ и WRITE добавляются случайным образом.
     *
     * @param user пользователь
     * @return множество прав доступа
     */
    private Set<Right> generateRightsForUser(User user) {
        if (user.isAdmin()) {
            return new HashSet<>(Arrays.asList(Right.READ, Right.WRITE, Right.GRANT));
        }
        Set<Right> rights = new HashSet<>();
        rights.add(Right.GRANT);
        if (random.nextBoolean()) {
            rights.add(Right.READ);
        }
        if (random.nextBoolean()) {
            rights.add(Right.WRITE);
        }
        return rights;
    }

    /**
     * Проверяет, имеет ли пользователь указанное право на объект с заданным именем.
     *
     * @param user         пользователь
     * @param objectName   имя объекта
     * @param requiredRight требуемое право
     * @return true если пользователь имеет право, иначе false
     */
    public boolean checkAccess(User user, String objectName, Right requiredRight) {
        Optional<SystemObject> object = objectRepository.findByName(objectName);
        return object.filter(o -> accessMatrixRepository.hasRight(user, o, requiredRight)).isPresent();
    }

    /**
     * Проверяет, приведёт ли передача указанного права к тому, что целевой пользователь
     * станет администратором (получит READ, WRITE и GRANT на все объекты).
     *
     * @param toUser     целевой пользователь
     * @param objectName имя объекта
     * @param right      передаваемое право
     * @return true если передача сделает пользователя администратором, иначе false
     */
    public boolean wouldTransferMakeAdmin(User toUser, String objectName, Right right) {
        Optional<SystemObject> obj = objectRepository.findByName(objectName);
        if (obj.isEmpty()) return false;
        return wouldBecomeAdmin(toUser, obj.get(), right);
    }

    /**
     * Внутренний метод для проверки, станет ли пользователь администратором после получения права.
     *
     * @param target       целевой пользователь
     * @param targetObject целевой объект
     * @param newRight     новое право
     * @return true если пользователь станет администратором
     */
    private boolean wouldBecomeAdmin(User target, SystemObject targetObject, Right newRight) {
        if (target.isAdmin()) return false;

        for (SystemObject obj : objectRepository.findAll()) {
            if (obj.equals(targetObject)) continue;
            if (!hasAllRights(target, obj)) {
                return false;
            }
        }

        Set<Right> currentRights = accessMatrixRepository.getRights(target, targetObject);
        Set<Right> afterRights = new HashSet<>(currentRights);
        afterRights.add(newRight);
        return afterRights.containsAll(List.of(Right.READ, Right.WRITE, Right.GRANT));
    }

    /**
     * Проверяет, имеет ли пользователь все права (READ, WRITE, GRANT) на указанный объект.
     *
     * @param user   пользователь
     * @param object объект
     * @return true если пользователь имеет все права
     */
    private boolean hasAllRights(User user, SystemObject object) {
        Set<Right> rights = accessMatrixRepository.getRights(user, object);
        return rights.containsAll(List.of(Right.READ, Right.WRITE, Right.GRANT));
    }

    /**
     * Выполняет операцию передачи права от одного пользователя другому.
     * Проверяет наличие права на передачу у отправителя, существование получателя и объекта,
     * а также не приведёт ли передача к появлению нового администратора.
     *
     * @param from        пользователь, передающий право
     * @param objectName  имя объекта
     * @param right       передаваемое право
     * @param toUserName  имя получателя
     * @return true если передача выполнена успешно, иначе false
     */
    public boolean transferRight(User from, String objectName, Right right, String toUserName) {
        if (right == null || right.isDenied()) return false;
        Optional<SystemObject> object = objectRepository.findByName(objectName);
        Optional<User> toUser = userRepository.findByName(toUserName);
        if (object.isEmpty() || toUser.isEmpty()) return false;

        if (!from.isAdmin() && !accessMatrixRepository.hasRight(from, object.get(), Right.GRANT))
            return false;
        if (!from.isAdmin() && !accessMatrixRepository.hasRight(from, object.get(), right))
            return false;

        if (wouldBecomeAdmin(toUser.get(), object.get(), right)) {
            return false;
        }

        if (accessMatrixRepository.hasRight(toUser.get(), object.get(), right)) {
            return false;
        }

        accessMatrixRepository.addRight(toUser.get(), object.get(), right);
        return true;
    }

    /**
     * Возвращает карту прав указанного пользователя на все объекты.
     *
     * @param user пользователь
     * @return отображение "объект -> множество прав"
     */
    public Map<SystemObject, Set<Right>> getUserRights(User user) {
        Map<SystemObject, Set<Right>> result = new LinkedHashMap<>();
        for (SystemObject object : objectRepository.findAll()) {
            result.put(object, accessMatrixRepository.getRights(user, object));
        }
        return result;
    }

    /**
     * Выполняет поиск объекта по имени.
     *
     * @param name имя объекта
     * @return Optional с объектом или пустой Optional
     */
    public Optional<SystemObject> findObject(String name) {
        return objectRepository.findByName(name);
    }

    /**
     * Выполняет поиск пользователя по имени.
     *
     * @param name имя пользователя
     * @return Optional с пользователем или пустой Optional
     */
    public Optional<User> findUser(String name) {
        return userRepository.findByName(name);
    }

    /**
     * Возвращает полную матрицу прав доступа для всех пользователей и объектов.
     *
     * @return отображение "пользователь -> (объект -> множество прав)"
     */
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