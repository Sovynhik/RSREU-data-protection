package ru.rsreu.sovynhik.pract_03.ui;

import ru.rsreu.sovynhik.pract_03.config.Constants;
import ru.rsreu.sovynhik.pract_03.model.*;
import ru.rsreu.sovynhik.pract_03.service.*;
import java.util.*;

public class ConsoleUI {
    private final AuthenticationService authenticationService;
    private final AccessMatrixService accessMatrixService;
    private final AuthorizationService authorizationService;
    private final Scanner scanner;

    public ConsoleUI(AuthenticationService authenticationService,
                     AccessMatrixService accessMatrixService,
                     AuthorizationService authorizationService) {
        this.authenticationService = authenticationService;
        this.accessMatrixService = accessMatrixService;
        this.authorizationService = authorizationService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printWelcomeMessage();
        while (true) {
            Optional<User> currentUser = login();
            currentUser.ifPresent(this::userSession);
        }
    }

    private void printWelcomeMessage() {
        System.out.println(Constants.MSG_WELCOME);
        System.out.printf(Constants.MSG_STATS + "\n",
                Constants.USER_COUNT, Constants.OBJECT_COUNT);
        System.out.println(Constants.MSG_SEPARATOR);
    }

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

    private void showUserRights(User user) {
        System.out.println(Constants.MSG_YOUR_RIGHTS);
        Map<SystemObject, Set<Right>> rights = accessMatrixService.getUserRights(user);
        for (Map.Entry<SystemObject, Set<Right>> entry : rights.entrySet()) {
            String rightsString = formatRights(entry.getValue());
            System.out.printf(Constants.MSG_RIGHTS_FORMAT + "\n",
                    entry.getKey().getName(), rightsString);
        }
    }

    private String formatRights(Set<Right> rights) {
        if (rights.contains(Right.DENIED)) return Constants.RIGHT_DENIED;
        if (rights.isEmpty()) return Constants.MSG_NO_RIGHTS;
        List<String> rightNames = rights.stream()
                .map(Right::getDisplayName)
                .sorted()
                .toList();
        return String.join(", ", rightNames);
    }

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
                default:
                    System.out.println(Constants.MSG_UNKNOWN_COMMAND);
            }
        }
    }

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
}