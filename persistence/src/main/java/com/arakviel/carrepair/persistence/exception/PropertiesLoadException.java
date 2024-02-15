package com.arakviel.carrepair.persistence.exception;

/**
 * Виключення при помилці завантаження файлу типу "<code>*.properties</code>".
 *
 * <p>Наприклад, при відсутності файлу, або не валідному шляху.
 */
public class PropertiesLoadException extends RuntimeException {

    public PropertiesLoadException(String message) {
        super(message);
    }
}
