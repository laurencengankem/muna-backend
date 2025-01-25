package com.example.kulvida.domain.enums;

public enum UserRole {

    USER("USER"),
    OPERATOR("OPERATOR"),
    ADMIN("ADMIN"),
    TECH("TECH");

    private final String value;


    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
