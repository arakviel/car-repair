package com.arakviel.carrepair.persistence.impl;

import static java.util.stream.Collectors.joining;

import com.arakviel.carrepair.persistence.dao.CurrencyDao;
import com.arakviel.carrepair.persistence.entity.impl.CurrencyEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.PersistenceException;
import com.arakviel.carrepair.persistence.filter.impl.CurrencyFilterDto;
import com.arakviel.carrepair.persistence.impl.converter.impl.CurrencyEntityRowConverter;
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

public class CurrencyDaoImpl implements CurrencyDao {

    public static final Logger LOGGER = LoggerFactory.getLogger(CurrencyDaoImpl.class);

    private static final String FIND_ALL_SQL =
            """
            SELECT id,
                   name,
                   symbol
              FROM currencies
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;

    private static final String SAVE_SQL =
            """
            INSERT INTO currencies(name, symbol)
            VALUES (?, ?);
            """;

    private static final String UPDATE_SQL =
            """
            UPDATE currencies
               SET name = ?,
                   symbol = ?
             WHERE id = ?;
            """;

    private static final String REMOVE_SQL =
            """
            DELETE FROM currencies
                  WHERE id = ?;
            """;

    /**
     * Get an CurrencyEntity object by identifier.
     *
     * @param id primary key identifier
     * @return Optional<CurrencyEntity>
     */
    @Override
    public Optional<CurrencyEntity> findOneById(Integer id) {
        try (var connection = ConnectionManager.get()) {
            return findOneById(id, connection);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(CurrencyDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new EntityNotFoundException(message);
        }
    }

    /**
     * Get an entity object by identifier.
     *
     * @param id primary key identifier
     * @param connection JDBC Connection
     * @return Optional<EmployeeEntity>
     */
    @Override
    public Optional<CurrencyEntity> findOneById(Integer id, Connection connection) {
        try (var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setInt(1, id);
            var resultSet = statement.executeQuery();
            CurrencyEntity currencyEntity = null;
            if (resultSet.next()) {
                currencyEntity = CurrencyEntityRowConverter.getInstance().execute(resultSet);

                LOGGER.info("Founded entity {} by id = {}. Запит: {}", currencyEntity, id, FIND_BY_ID_SQL);
            }
            return Optional.ofNullable(currencyEntity);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(CurrencyDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<CurrencyEntity> findAll() {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = statement.executeQuery();
            var currencyEntities = new ArrayList<CurrencyEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                currencyEntities.add(CurrencyEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", currencyEntities.size(), FIND_ALL_SQL);
            return currencyEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів в %s. Детально: %s"
                    .formatted(CurrencyDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<CurrencyEntity> findAll(CurrencyFilterDto filter) {
        var parameters = new ArrayList<>();
        var whereSQL = new ArrayList<String>();

        if (Objects.nonNull(filter.name())) {
            whereSQL.add("name LIKE ?");
            parameters.add("%" + filter.name() + "%");
        }
        if (Objects.nonNull(filter.symbol())) {
            whereSQL.add("symbol = ?");
            parameters.add(filter.symbol());
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
            var currencyEntities = new ArrayList<CurrencyEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                currencyEntities.add(CurrencyEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей після фільтрації: {}. Запит: {}", currencyEntities.size(), sql);
            return currencyEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів за фільтром %s в %s. Детально: %s"
                    .formatted(where, CurrencyDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Save or update the currencyEntity to the database table.
     *
     * @param currencyEntity persistent currencyEntity
     * @return currencyEntity, with identifier of the last added record from the database
     */
    @Override
    public CurrencyEntity save(CurrencyEntity currencyEntity) {
        boolean isNullId = Objects.isNull(currencyEntity.getId());
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(
                        isNullId ? SAVE_SQL : UPDATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, currencyEntity.getName());
            statement.setString(2, currencyEntity.getSymbol());

            if (isNullId) {
                LOGGER.info("Формування запиту на збереження.");
            } else {
                statement.setInt(3, currencyEntity.getId());

                LOGGER.info("Формування запиту на оновлення.");
            }
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                currencyEntity.setId(generatedKeys.getInt(1));
            }

            LOGGER.info("Успішне оновлення або збереження об'єкту: {}", currencyEntity);
            return currencyEntity;
        } catch (SQLException e) {
            String message = "При збереженні або оновленні запису в %s. Детально: %s"
                    .formatted(CurrencyDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Delete a currency from the database table by identifier.
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
                    .formatted(CurrencyDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    private CurrencyDaoImpl() {}

    private static class SingletonHolder {
        public static final CurrencyDaoImpl INSTANCE = new CurrencyDaoImpl();
    }

    public static CurrencyDaoImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
