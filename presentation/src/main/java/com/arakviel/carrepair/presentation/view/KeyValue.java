package com.arakviel.carrepair.presentation.view;

public record KeyValue(String key, String value) {

    @Override
    public String toString() {
        return value;
    }
}
