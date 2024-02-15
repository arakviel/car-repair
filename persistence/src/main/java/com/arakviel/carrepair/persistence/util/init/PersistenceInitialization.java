package com.arakviel.carrepair.persistence.util.init;

import com.arakviel.carrepair.persistence.exception.persistance.PersistenceException;
import com.arakviel.carrepair.persistence.util.ConnectionManager;
import com.arakviel.carrepair.persistence.util.PersistenceConfig;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.stream.Collectors;

public final class PersistenceInitialization {

    private static final String TYPE_KEY = "db.type";

    /** Start the database migration. Create a schema for car_repair. */
    public static void run() {
        try (Connection connection = ConnectionManager.get();
                Statement statement = connection.createStatement()) {
            String ddl = PersistenceConfig.get(TYPE_KEY).equals("local") ? "ddl.sql" : "ddl_remote.sql";
            String dml = PersistenceConfig.get(TYPE_KEY).equals("local") ? "dml.sql" : "dml_remote.sql";
            statement.execute(getSQL(ddl));
            statement.executeUpdate(getSQL(dml));
        } catch (SQLException e) {
            throw new PersistenceException(
                    "Помилка ініціалізації схеми БД та даних до таблиць. Детально: %s".formatted(e.getMessage()));
        }
    }

    private static String getSQL(final String resourceName) {
        return new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                        ConnectionManager.class.getClassLoader().getResourceAsStream(resourceName))))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    private PersistenceInitialization() {}
}
