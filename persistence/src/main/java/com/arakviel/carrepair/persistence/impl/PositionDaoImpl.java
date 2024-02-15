package com.arakviel.carrepair.persistence.impl;

import static java.util.stream.Collectors.joining;

import com.arakviel.carrepair.persistence.dao.PositionDao;
import com.arakviel.carrepair.persistence.entity.impl.PositionEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.PersistenceException;
import com.arakviel.carrepair.persistence.filter.impl.PositionFilterDto;
import com.arakviel.carrepair.persistence.impl.converter.impl.PositionEntityRowConverter;
import com.arakviel.carrepair.persistence.util.ConnectionManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PositionDaoImpl implements PositionDao {

    public static final Logger LOGGER = LoggerFactory.getLogger(PositionDaoImpl.class);

    private static final String FIND_ALL_SQL =
            """
            SELECT id,
                   name,
                   description,
                   currency_id,
                   salary_per_hour_whole_part,
                   salary_per_hour_decimal_part
              FROM positions
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;
    private static final String SAVE_SQL =
            """
            INSERT INTO positions(name, description, currency_id, salary_per_hour_whole_part, salary_per_hour_decimal_part)
            VALUES (?, ?, ?, ?, ?);
            """;
    private static final String UPDATE_SQL =
            """
            UPDATE positions
               SET name = ?,
                   description = ?,
                   currency_id = ?,
                   salary_per_hour_whole_part = ?,
                   salary_per_hour_decimal_part = ?
             WHERE id = ?;
            """;

    private static final String REMOVE_SQL =
            """
            DELETE FROM positions
                  WHERE id = ?;
            """;

    /**
     * Get an PositionEntity object by identifier.
     *
     * @param id primary key identifier
     * @return Optional<PositionEntity>
     */
    @Override
    public Optional<PositionEntity> findOneById(Integer id) {
        try (var connection = ConnectionManager.get()) {
            return findOneById(id, connection);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(PositionDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new EntityNotFoundException(message);
        }
    }

    /**
     * Get an entity object by identifier.
     *
     * @param id primary key identifier
     * @param connection JDBC Connection
     * @return Optional<PositionEntity>
     */
    @Override
    public Optional<PositionEntity> findOneById(Integer id, Connection connection) {
        try (var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setInt(1, id);
            var resultSet = statement.executeQuery();
            PositionEntity positionEntity = null;
            if (resultSet.next()) {
                positionEntity = PositionEntityRowConverter.getInstance().execute(resultSet);

                LOGGER.info("Founded entity %s by id = %s. Запит: %s".formatted(positionEntity, id, FIND_BY_ID_SQL));
            }
            return Optional.ofNullable(positionEntity);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(PositionDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<PositionEntity> findAll() {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = statement.executeQuery();
            var positionEntities = new ArrayList<PositionEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                PositionEntity positionEntity =
                        PositionEntityRowConverter.getInstance().execute(resultSet);
                positionEntity.getRoleEntity();
                positionEntities.add(positionEntity);
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", positionEntities.size(), FIND_ALL_SQL);
            return positionEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів в %s. Детально: %s"
                    .formatted(PositionDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<PositionEntity> findAll(PositionFilterDto filter) {
        var parameters = new ArrayList<>();
        var whereSQL = new ArrayList<String>();

        if (Objects.nonNull(filter.name())) {
            whereSQL.add("name LIKE ?");
            parameters.add("%" + filter.name() + "%");
        }
        if (Objects.nonNull(filter.currencyEntity())) {
            whereSQL.add("currency_id = ?");
            parameters.add(filter.currencyEntity().getId());
        }
        if (Objects.nonNull(filter.salary()) && filter.salary().wholePart() != 0) {
            whereSQL.add("salary_per_hour_whole_part = ?");
            parameters.add(filter.salary().wholePart());
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
            var positionEntities = new ArrayList<PositionEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                positionEntities.add(PositionEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей після фільтрації: {}. Запит: {}", positionEntities.size(), sql);
            return positionEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів за фільтром %s в %s. Детально: %s"
                    .formatted(where, PositionDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Save or update the positionEntity to the database table.
     *
     * @param positionEntity persistent positionEntity
     * @return positionEntity, with identifier of the last added record from the database
     */
    @Override
    public PositionEntity save(PositionEntity positionEntity) {
        boolean isNullId = Objects.isNull(positionEntity.getId());
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(
                        isNullId ? SAVE_SQL : UPDATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, positionEntity.getName());
            statement.setString(2, positionEntity.getDescription());
            statement.setInt(3, positionEntity.getCurrencyEntity().getId());
            statement.setInt(4, positionEntity.getSalaryPerHour().wholePart());
            statement.setInt(5, positionEntity.getSalaryPerHour().decimalPart());

            if (isNullId) {
                LOGGER.info("Формування запиту на збереження.");
            } else {
                statement.setInt(6, positionEntity.getId());

                LOGGER.info("Формування запиту на оновлення.");
            }
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                positionEntity.setId(generatedKeys.getInt(1));
            }

            // Скидання roleEntity, якщо він не порожній
            if (Objects.nonNull(positionEntity.getRoleEntity())) {
                positionEntity.setRoleEntity(null);
            }

            LOGGER.info("Успішне оновлення або збереження об'єкту: {}", positionEntity);
            return positionEntity;
        } catch (SQLException e) {
            String message = "При збереженні або оновленні запису в %s. Детально: %s"
                    .formatted(PositionDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Delete a position from the database table by identifier.
     *
     * @param id primary key identifier
     */
    @Override
    public void remove(Integer id) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(REMOVE_SQL)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            String message = "Помилка при операції видалення рядка таблиці в %s. Детально: %s"
                    .formatted(DiscountDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    private PositionDaoImpl() {}

    private static class SingletonHolder {
        public static final PositionDaoImpl INSTANCE = new PositionDaoImpl();
    }

    public static PositionDaoImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
