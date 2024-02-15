package com.arakviel.carrepair.persistence.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PersistenceConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceConfig.class);

    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    /**
     * Gets the value of the property by key
     *
     * @param key of property
     * @return property value
     */
    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    /**
     * Method for loading properties from a file.
     *
     * <p>Gets an object of class InputStream from the file <code>application.properties</code>
     *
     * <p>And passes this object to PROPERTIES.
     */
    private static void loadProperties() {
        try (InputStream applicationProperties =
                PersistenceConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            PROPERTIES.load(applicationProperties);
        } catch (IOException e) {
            LOGGER.error("error reading the property file: %s.".formatted(e.getMessage()));
            throw new RuntimeException(e.getMessage());
        }
    }

    private PersistenceConfig() {}
}
