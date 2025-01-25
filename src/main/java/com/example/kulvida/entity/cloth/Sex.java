package com.example.kulvida.entity.cloth;

public enum Sex {

    MALE("MALE"),
    FEMALE("FEMALE"),
    UNISEX("UNISEX");

    private final String value;


    Sex(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
