package ru.rsreu.sovynhik.pract_03.ui;

public class CommandParser {

    public static class CommandResult {
        private final String command;
        private final String[] args;

        public CommandResult(String command, String[] args) {
            this.command = command != null ? command.toLowerCase() : "";
            this.args = args != null ? args : new String[0];
        }

        public String getCommand() {
            return command;
        }

        public String[] getArgs() {
            return args;
        }

        public boolean hasArgs() {
            return args.length > 0;
        }

        public int getArgCount() {
            return args.length;
        }
    }

    public CommandResult parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new CommandResult("", new String[0]);
        }

        String[] parts = input.trim().split("\\s+");
        String command = parts[0];
        String[] args = new String[parts.length - 1];

        if (parts.length > 1) {
            System.arraycopy(parts, 1, args, 0, parts.length - 1);
        }

        return new CommandResult(command, args);
    }
}