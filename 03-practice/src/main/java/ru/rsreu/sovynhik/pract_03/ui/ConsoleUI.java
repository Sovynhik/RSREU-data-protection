package ru.rsreu.sovynhik.pract_03.ui;

import ru.rsreu.sovynhik.pract_03.config.Constants;
import ru.rsreu.sovynhik.pract_03.model.*;
import ru.rsreu.sovynhik.pract_03.service.*;
import java.util.*;

/**
 * Консольный пользовательский интерфейс для взаимодействия с системой дискреционного управления доступом (DAC).
 * Обеспечивает аутентификацию пользователей, выполнение операций чтения, записи, передачи прав
 * и отображение текущей матрицы доступа.
 */
public class ConsoleUI {
    private final AuthenticationService authenticationService;
    private final AccessMatrixService accessMatrixService;
    private final AuthorizationService authorizationService;
    private final Scanner scanner;

    /**
     * Создает консольный интерфейс с указанными сервисами.
     *
     * @param authenticationService сервис аутентификации
     * @param accessMatrixService   сервис матрицы доступа
     * @param authorizationService  сервис авторизации
     */
    public ConsoleUI(AuthenticationService authenticationService,
                     AccessMatrixService accessMatrixService,
                     AuthorizationService authorizationService) {
        this.authenticationService = authenticationService;
        this.accessMatrixService = accessMatrixService;
        this.authorizationService = authorizationService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Запускает основной цикл работы приложения.
     * После каждого завершения сессии пользователя начинается новый цикл аутентификации.
     */
    public void start() {
        printWelcomeMessage();
        while (true) {
            Optional<User> currentUser = login();
            currentUser.ifPresent(this::userSession);
        }
    }

    /**
     * Выводит приветственное сообщение и разделитель.
     */
    private void printWelcomeMessage() {
        System.out.println(Constants.MSG_WELCOME);
        System.out.println(Constants.MSG_SEPARATOR);
    }

    /**
     * Выполняет аутентификацию пользователя.
     * Запрашивает имя пользователя и проверяет его наличие в системе.
     * При успешной аутентификации выводит приветствие и права пользователя.
     *
     * @return {@link Optional} с аутентифицированным пользователем или пустой Optional при неудаче
     */
    private Optional<User> login() {
        System.out.print(Constants.MSG_ENTER_USER);
        String userId = scanner.nextLine().trim();
        Optional<User> user = authenticationService.authenticate(userId);
        if (user.isPresent()) {
            String adminSuffix = user.get().isAdmin() ? " " + Constants.MSG_ADMIN_SUFFIX : "";
            System.out.printf(Constants.MSG_AUTH_SUCCESS + "\n",
                    user.get().getName(), adminSuffix);
            showUserRights(user.get());
            return user;
        } else {
            System.out.println(Constants.MSG_AUTH_FAIL);
            return Optional.empty();
        }
    }

    /**
     * Отображает права текущего пользователя на все объекты.
     *
     * @param user пользователь
     */
    private void showUserRights(User user) {
        System.out.println(Constants.MSG_YOUR_RIGHTS);
        Map<SystemObject, Set<Right>> rights = accessMatrixService.getUserRights(user);
        for (Map.Entry<SystemObject, Set<Right>> entry : rights.entrySet()) {
            String rightsString = formatRights(entry.getValue());
            System.out.printf(Constants.MSG_RIGHTS_FORMAT + "\n",
                    entry.getKey().getName(), rightsString);
        }
    }

    /**
     * Форматирует множество прав в строку для отображения.
     * Особые случаи: {@link Right#DENIED} и пустое множество.
     *
     * @param rights множество прав
     * @return отформатированная строка
     */
    private String formatRights(Set<Right> rights) {
        if (rights.contains(Right.DENIED)) return Constants.RIGHT_DENIED;
        if (rights.isEmpty()) return Constants.MSG_NO_RIGHTS;
        List<String> rightNames = rights.stream()
                .map(Right::getDisplayName)
                .sorted()
                .toList();
        return String.join(", ", rightNames);
    }

    /**
     * Управляет сессией аутентифицированного пользователя.
     * В цикле обрабатывает вводимые пользователем команды до выхода.
     *
     * @param user текущий пользователь
     */
    private void userSession(User user) {
        while (true) {
            System.out.print(Constants.MSG_PROMPT);
            String command = scanner.nextLine().trim().toLowerCase();

            if (command.equals(Constants.CMD_QUIT)) {
                System.out.printf(Constants.MSG_GOODBYE + "\n", user.getName());
                break;
            }

            switch (command) {
                case Constants.CMD_READ:
                    processRead(user);
                    break;
                case Constants.CMD_WRITE:
                    processWrite(user);
                    break;
                case Constants.CMD_GRANT:
                    processGrant(user);
                    break;
                case Constants.CMD_SHOW:
                    processShow();
                    break;
                default:
                    System.out.println(Constants.MSG_UNKNOWN_COMMAND);
            }
        }
    }

    /**
     * Обрабатывает команду чтения.
     * Запрашивает имя объекта и проверяет право на чтение.
     *
     * @param user текущий пользователь
     */
    private void processRead(User user) {
        System.out.print(Constants.PROMPT_READ_OBJECT);
        String objectName = scanner.nextLine().trim();
        Optional<SystemObject> object = accessMatrixService.findObject(objectName);
        if (object.isEmpty()) {
            System.out.println(Constants.MSG_OBJECT_NOT_FOUND);
            return;
        }
        if (authorizationService.canRead(user, objectName)) {
            System.out.println(Constants.MSG_ACCESS_GRANTED);
        } else {
            System.out.println(Constants.MSG_ACCESS_DENIED);
        }
    }

    /**
     * Обрабатывает команду записи.
     * Запрашивает имя объекта и проверяет право на запись.
     *
     * @param user текущий пользователь
     */
    private void processWrite(User user) {
        System.out.print(Constants.PROMPT_WRITE_OBJECT);
        String objectName = scanner.nextLine().trim();
        Optional<SystemObject> object = accessMatrixService.findObject(objectName);
        if (object.isEmpty()) {
            System.out.println(Constants.MSG_OBJECT_NOT_FOUND);
            return;
        }
        if (authorizationService.canWrite(user, objectName)) {
            System.out.println(Constants.MSG_ACCESS_GRANTED);
        } else {
            System.out.println(Constants.MSG_ACCESS_DENIED);
        }
    }

    /**
     * Обрабатывает команду передачи прав.
     * Запрашивает объект, право и целевого пользователя, выполняет проверки и передачу.
     *
     * @param user текущий пользователь (источник прав)
     */
    private void processGrant(User user) {
        System.out.print(Constants.PROMPT_GRANT_OBJECT);
        String objectName = scanner.nextLine().trim();
        Optional<SystemObject> object = accessMatrixService.findObject(objectName);
        if (object.isEmpty()) {
            System.out.println(Constants.MSG_OBJECT_NOT_FOUND);
            return;
        }

        System.out.print(Constants.PROMPT_GRANT_RIGHT);
        String rightStr = scanner.nextLine().trim();
        Right right = Right.fromString(rightStr);
        if (right == null) {
            System.out.println(Constants.MSG_RIGHT_NOT_FOUND);
            return;
        }

        System.out.print(Constants.PROMPT_GRANT_USER);
        String targetUserName = scanner.nextLine().trim();
        Optional<User> targetUser = accessMatrixService.findUser(targetUserName);
        if (targetUser.isEmpty()) {
            System.out.println(Constants.MSG_USER_NOT_FOUND);
            return;
        }

        if (accessMatrixService.checkAccess(targetUser.get(), objectName, right)) {
            System.out.println(Constants.MSG_RIGHT_ALREADY_EXISTS);
            return;
        }

        if (accessMatrixService.wouldTransferMakeAdmin(targetUser.get(), objectName, right)) {
            System.out.println(Constants.MSG_CANT_MAKE_ADMIN);
            return;
        }

        boolean success = accessMatrixService.transferRight(user, objectName, right, targetUserName);
        if (success) {
            System.out.println(Constants.MSG_ACCESS_GRANTED);
        } else {
            if (!authorizationService.canGrant(user, objectName)) {
                System.out.println(Constants.MSG_NO_GRANT_RIGHT);
            } else {
                System.out.println(Constants.MSG_NO_RIGHT_TO_TRANSFER);
            }
        }
    }

    /**
     * Отображает полную матрицу доступа для всех пользователей и объектов.
     */
    private void processShow() {
        Map<User, Map<SystemObject, Set<Right>>> matrix = accessMatrixService.getAllRightsMatrix();
        System.out.println("\nМатрица доступа:");
        for (Map.Entry<User, Map<SystemObject, Set<Right>>> userEntry : matrix.entrySet()) {
            User user = userEntry.getKey();
            System.out.print(user.getName() + (user.isAdmin() ? " (админ)" : "") + ": ");
            for (Map.Entry<SystemObject, Set<Right>> objEntry : userEntry.getValue().entrySet()) {
                String rightsStr = formatRights(objEntry.getValue());
                System.out.print(objEntry.getKey().getName() + "=[" + rightsStr + "] ");
            }
            System.out.println();
        }
    }
}