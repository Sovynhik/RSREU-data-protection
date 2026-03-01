package ru.rsreu.sovynhik.lab_03.service;

import ru.rsreu.sovynhik.lab_03.config.Constants;
import ru.rsreu.sovynhik.lab_03.model.Right;
import ru.rsreu.sovynhik.lab_03.model.User;
import ru.rsreu.sovynhik.lab_03.ui.CommandParser.CommandResult;

public class CommandProcessor {
    private final AuthorizationService authorizationService;
    private final AccessMatrixService accessMatrixService;

    public CommandProcessor(AuthorizationService authorizationService,
                            AccessMatrixService accessMatrixService) {
        this.authorizationService = authorizationService;
        this.accessMatrixService = accessMatrixService;
    }

    public String processCommand(User currentUser, CommandResult command) {
        if (command == null || command.getCommand() == null || command.getCommand().isEmpty()) {
            return Constants.MSG_ENTER_COMMAND;
        }

        switch (command.getCommand()) {
            case Constants.CMD_READ:
                return processRead(currentUser, command.getArgs());
            case Constants.CMD_WRITE:
                return processWrite(currentUser, command.getArgs());
            case Constants.CMD_GRANT:
                return processGrant(currentUser, command.getArgs());
            default:
                return Constants.MSG_UNKNOWN_COMMAND;
        }
    }

    private String processRead(User user, String[] args) {
        if (args.length < 1) {
            return String.format(Constants.MSG_INVALID_FORMAT, "read объект");
        }

        String objectName = args[0];
        if (accessMatrixService.findObject(objectName).isEmpty()) {
            return Constants.MSG_OBJECT_NOT_FOUND;
        }

        return authorizationService.canRead(user, objectName)
                ? Constants.MSG_ACCESS_GRANTED
                : Constants.MSG_ACCESS_DENIED;
    }

    private String processWrite(User user, String[] args) {
        if (args.length < 1) {
            return String.format(Constants.MSG_INVALID_FORMAT, "write объект");
        }

        String objectName = args[0];
        if (accessMatrixService.findObject(objectName).isEmpty()) {
            return Constants.MSG_OBJECT_NOT_FOUND;
        }

        return authorizationService.canWrite(user, objectName)
                ? Constants.MSG_ACCESS_GRANTED
                : Constants.MSG_ACCESS_DENIED;
    }

    private String processGrant(User user, String[] args) {
        if (args.length < 3) {
            return String.format(Constants.MSG_INVALID_FORMAT, Constants.GRANT_FORMAT);
        }

        String objectName = args[0];
        Right right = Right.fromString(args[1]);
        String targetUser = args[2];

        // Проверка существования объекта
        if (accessMatrixService.findObject(objectName).isEmpty()) {
            return Constants.MSG_OBJECT_NOT_FOUND;
        }

        // Проверка существования пользователя
        if (accessMatrixService.findUser(targetUser).isEmpty()) {
            return Constants.MSG_USER_NOT_FOUND;
        }

        // Проверка корректности права
        if (right == null) {
            return Constants.MSG_RIGHT_NOT_FOUND;
        }

        // Выполнение передачи
        boolean success = accessMatrixService.transferRight(user, objectName, right, targetUser);

        if (success) {
            return String.format(Constants.MSG_GRANT_SUCCESS,
                    right.getDisplayName(), objectName, targetUser);
        } else {
            // Определяем причину отказа
            if (!authorizationService.canGrant(user, objectName)) {
                return Constants.MSG_NO_GRANT_RIGHT;
            }
            return Constants.MSG_NO_RIGHT_TO_TRANSFER;
        }
    }
}