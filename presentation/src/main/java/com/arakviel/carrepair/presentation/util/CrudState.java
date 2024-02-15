package com.arakviel.carrepair.presentation.util;

public enum CrudState {
    ADD("Додати"),
    EDIT("Редагувати"),
    REMOVE("Видалити");

    private String value;

    CrudState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
