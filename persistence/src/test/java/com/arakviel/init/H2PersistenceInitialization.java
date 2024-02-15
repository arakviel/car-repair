package com.arakviel.init;

import com.arakviel.carrepair.persistence.util.ConnectionManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.stream.Collectors;

public final class H2PersistenceInitialization {

    /** Start the database migration. Create a schema for H2 car_repair. */
    public static void run(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(getSQL(Path.of("h2", "migrations", "ddl.sql").toString()));
            statement.executeUpdate(
                    getSQL(Path.of("h2", "migrations", "dml.sql").toString()));
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }

    private static String getSQL(final String resourceName) {
        return new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                        ConnectionManager.class.getClassLoader().getResourceAsStream(resourceName))))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    private H2PersistenceInitialization() {}
}
