package com.arakviel.carrepair.persistence.impl;

import static java.util.stream.Collectors.joining;

import com.arakviel.carrepair.persistence.dao.UserDao;
import com.arakviel.carrepair.persistence.entity.impl.UserEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.PersistenceException;
import com.arakviel.carrepair.persistence.filter.impl.UserFilterDto;
import com.arakviel.carrepair.persistence.impl.converter.impl.UserEntityRowConverter;
import com.arakviel.carrepair.persistence.util.ConnectionManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDaoImpl implements UserDao {

    public static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);

    private static final String FIND_ALL_SQL =
            """
            SELECT employee_id,
                   email,
                   login,
                   password
              FROM users
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE employee_id = ?
            """;
    private static final String SAVE_SQL =
            """
            INSERT INTO users(employee_id, email, login, password)
            VALUES (?, ?, ?, ?);
            """;
    private static final String UPDATE_SQL =
            """
            UPDATE users
               SET email = ?,
                   login = ?,
                   password = ?
             WHERE employee_id = ?;
            """;

    private static final String REMOVE_SQL =
            """
            DELETE FROM users
                  WHERE employee_id = ?;
            """;

    /**
     * Get an SpareEntity object by identifier.
     *
     * @param id primary key identifier
     * @return Optional<SpareEntity>
     */
    @Override
    public Optional<UserEntity> findOneById(UUID id) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setString(1, id.toString());
            var resultSet = statement.executeQuery();
            UserEntity userEntity = null;
            if (resultSet.next()) {
                userEntity = UserEntityRowConverter.getInstance().execute(resultSet);

                LOGGER.info(
                        "Founded entity %s by id = %s. Запит: %s".formatted(userEntity, id.toString(), FIND_BY_ID_SQL));
            }
            return Optional.ofNullable(userEntity);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(UserDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new EntityNotFoundException(message);
        }
    }

    /**
     * Get the entire collection of entities.
     *
     * @return collection of Entities
     */
    @Override
    public List<UserEntity> findAll() {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = statement.executeQuery();
            var userEntities = new ArrayList<UserEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                userEntities.add(UserEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", userEntities.size(), FIND_ALL_SQL);
            return userEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів в %s. Детально: %s"
                    .formatted(UserDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @param filter values for where conditions
     * @return collection of Entities
     */
    @Override
    public List<UserEntity> findAll(UserFilterDto filter) {
        var parameters = new ArrayList<>();
        var whereSQL = new ArrayList<String>();

        if (Objects.nonNull(filter.email())) {
            whereSQL.add("email LIKE ?");
            parameters.add("%" + filter.email() + "%");
        }
        if (Objects.nonNull(filter.login())) {
            whereSQL.add("login LIKE ?");
            parameters.add("%" + filter.login() + "%");
        }

        String where = "";
        if (!whereSQL.isEmpty()) {
            where = whereSQL.stream().collect(joining(" AND ", " WHERE ", ""));
        }
        var sql = FIND_ALL_SQL + where;

        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }
            var resultSet = statement.executeQuery();
            var userEntities = new ArrayList<UserEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                userEntities.add(UserEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей після фільтрації: {}. Запит: {}", userEntities.size(), sql);
            return userEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів за фільтром %s в %s. Детально: %s"
                    .formatted(where, UserDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Save or update the userEntity to the database table.
     *
     * @param userEntity persistent spare
     * @return userEntity, with identifier of the last added record from the database
     */
    @Override
    public UserEntity save(UserEntity userEntity) {
        boolean isNullId = Objects.isNull(userEntity.getId());
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(isNullId ? SAVE_SQL : UPDATE_SQL)) {
            if (isNullId) {
                var id = userEntity.getEmployeeEntity().getId();
                userEntity.setId(id);
                statement.setString(1, id.toString());
                statement.setString(2, userEntity.getEmail());
                statement.setString(3, userEntity.getLogin());
                statement.setString(4, userEntity.getPassword());

                LOGGER.info("Формування запиту на збереження.");
            } else {
                statement.setString(1, userEntity.getEmail());
                statement.setString(2, userEntity.getLogin());
                statement.setString(3, userEntity.getPassword());
                statement.setString(4, userEntity.getId().toString());

                LOGGER.info("Формування запиту на оновлення.");
            }
            statement.executeUpdate();

            // Скидання employeeEntity, якщо він не порожній
            if (Objects.nonNull(userEntity.getEmployeeEntity())) {
                userEntity.setEmployeeEntity(null);
            }

            LOGGER.info("Успішне оновлення або збереження об'єкту: {}", userEntity);
            return userEntity;
        } catch (SQLException e) {
            String message = "При збереженні або оновленні запису в %s. Детально: %s"
                    .formatted(UserDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Delete a spare from the database table by identifier.
     *
     * @param id primary key identifier
     */
    @Override
    public void remove(UUID id) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(REMOVE_SQL)) {
            statement.setString(1, id.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            String message = "Помилка при операції видалення рядка таблиці в %s. Детально: %s"
                    .formatted(UserDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    private UserDaoImpl() {}

    private static class SingletonHolder {
        public static final UserDaoImpl INSTANCE = new UserDaoImpl();
    }

    public static UserDaoImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
