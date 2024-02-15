package com.arakviel.carrepair.presentation.converter;

import com.arakviel.carrepair.presentation.view.KeyValue;
import javafx.util.StringConverter;

public class KeyValueStringConverter extends StringConverter<KeyValue> {

    @Override
    public String toString(KeyValue keyValue) {
        if (keyValue != null) {
            return keyValue.value();
        }
        return null;
    }

    @Override
    public KeyValue fromString(String string) {
        // Якщо потрібно, реалізуйте логіку конвертації рядка назад в KeyValue об'єкт
        return null;
    }
}
