package ru.rsreu.sovynhik.lab_03.model;

import ru.rsreu.sovynhik.lab_03.config.Constants;

public enum Right {
    READ(Constants.RIGHT_READ, Constants.ALIAS_READ_RU, Constants.ALIAS_READ_EN),
    WRITE(Constants.RIGHT_WRITE, Constants.ALIAS_WRITE_RU, Constants.ALIAS_WRITE_EN),
    GRANT(Constants.RIGHT_GRANT, Constants.ALIAS_GRANT_RU, Constants.ALIAS_GRANT_EN),
    DENIED(Constants.RIGHT_DENIED, Constants.RIGHT_DENIED);

    private final String displayName;
    private final String[] aliases;

    Right(String displayName, String... aliases) {
        this.displayName = displayName;
        this.aliases = aliases;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Right fromString(String text) {
        if (text == null) return null;

        for (Right right : Right.values()) {
            if (right.displayName.equalsIgnoreCase(text)) {
                return right;
            }
            for (String alias : right.aliases) {
                if (alias.equalsIgnoreCase(text)) {
                    return right;
                }
            }
        }
        return null;
    }

    public boolean isDenied() {
        return this == DENIED;
    }

    public boolean isRead() {
        return this == READ;
    }

    public boolean isWrite() {
        return this == WRITE;
    }

    public boolean isGrant() {
        return this == GRANT;
    }
}