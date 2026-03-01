package ru.rsreu.sovynhik.lab_03.ui;

import ru.rsreu.sovynhik.lab_03.config.Constants;
import ru.rsreu.sovynhik.lab_03.model.*;
import ru.rsreu.sovynhik.lab_03.service.*;
import java.util.*;

public class ConsoleUI {
    private final AuthenticationService authenticationService;
    private final AccessMatrixService accessMatrixService;
    private final CommandProcessor commandProcessor;
    private final CommandParser commandParser;
    private final Scanner scanner;

    public ConsoleUI(AuthenticationService authenticationService,
                     AccessMatrixService accessMatrixService,
                     CommandProcessor commandProcessor) {
        this.authenticationService = authenticationService;
        this.accessMatrixService = accessMatrixService;
        this.commandProcessor = commandProcessor;
        this.commandParser = new CommandParser();
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
        if (rights.contains(Right.DENIED)) {
            return Constants.RIGHT_DENIED;
        }
        if (rights.isEmpty()) {
            return Constants.MSG_NO_RIGHTS;
        }

        List<String> rightNames = rights.stream()
                .map(Right::getDisplayName)
                .sorted()
                .toList();

        return String.join(", ", rightNames);
    }

    private void userSession(User user) {
        while (true) {
            System.out.print(Constants.MSG_PROMPT);
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase(Constants.CMD_QUIT)) {
                System.out.printf(Constants.MSG_GOODBYE + "\n", user.getName());
                break;
            }

            if (input.isEmpty()) {
                continue;
            }

            CommandParser.CommandResult command = commandParser.parse(input);
            String result = commandProcessor.processCommand(user, command);
            System.out.println(result);
        }
    }
}