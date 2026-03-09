package ru.rsreu.sovynhik.pract_03.ui;

public class CommandParser {

    public record CommandResult(String command, String[] args) {
            public CommandResult(String command, String[] args) {
                this.command = command != null ? command.toLowerCase() : "";
                this.args = args != null ? args : new String[0];
            }
        }
}