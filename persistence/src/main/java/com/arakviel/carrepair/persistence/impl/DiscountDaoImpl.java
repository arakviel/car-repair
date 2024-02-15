package com.arakviel.carrepair.persistence.impl;

import static java.util.stream.Collectors.joining;

import com.arakviel.carrepair.persistence.dao.DiscountDao;
import com.arakviel.carrepair.persistence.entity.impl.DiscountEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.PersistenceException;
import com.arakviel.carrepair.persistence.filter.impl.DiscountFilterDto;
import com.arakviel.carrepair.persistence.impl.converter.impl.DiscountEntityRowConverter;
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

public class DiscountDaoImpl implements DiscountDao {

    public static final Logger LOGGER = LoggerFactory.getLogger(DiscountDaoImpl.class);

    private static final String FIND_ALL_SQL =
            """
            SELECT id,
                   name,
                   description,
                   value
              FROM discounts
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;

    private static final String SAVE_SQL =
            """
            INSERT INTO discounts(name, description, value)
            VALUES (?, ?, ?);
            """;

    private static final String UPDATE_SQL =
            """
            UPDATE discounts
               SET name = ?,
                   description = ?,
                   value = ?
             WHERE id = ?;
            """;

    private static final String REMOVE_SQL =
            """
            DELETE FROM discounts
                  WHERE id = ?;
            """;

    /**
     * Get an DiscountEntity object by identifier.
     *
     * @param id primary key identifier
     * @return Optional<DiscountEntity>
     */
    @Override
    public Optional<DiscountEntity> findOneById(Integer id) {
        try (var connection = ConnectionManager.get(); ) {
            return findOneById(id, connection);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(DiscountDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new EntityNotFoundException(message);
        }
    }

    /**
     * Get an entity object by identifier.
     *
     * @param id primary key identifier
     * @param connection JDBC Connection
     * @return Optional<DiscountEntity>
     */
    @Override
    public Optional<DiscountEntity> findOneById(Integer id, Connection connection) {
        try (var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setInt(1, id);
            var resultSet = statement.executeQuery();
            DiscountEntity discountEntity = null;
            if (resultSet.next()) {
                discountEntity = DiscountEntityRowConverter.getInstance().execute(resultSet);

                LOGGER.info("Founded entity {} by id = {}. Запит: {}", discountEntity, id, FIND_BY_ID_SQL);
            }
            return Optional.ofNullable(discountEntity);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(DiscountDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<DiscountEntity> findAll() {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = statement.executeQuery();
            var discountEntities = new ArrayList<DiscountEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                discountEntities.add(DiscountEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", discountEntities.size(), FIND_ALL_SQL);
            return discountEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів в %s. Детально: %s"
                    .formatted(DiscountDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<DiscountEntity> findAll(DiscountFilterDto filter) {
        var parameters = new ArrayList<>();
        var whereSQL = new ArrayList<String>();

        if (Objects.nonNull(filter.name())) {
            whereSQL.add("name LIKE ?");
            parameters.add("%" + filter.name() + "%");
        }
        if (filter.value() != 0) {
            whereSQL.add("value = ?");
            parameters.add(filter.value());
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
            var discountEntities = new ArrayList<DiscountEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                discountEntities.add(DiscountEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей після фільтрації: {}. Запит: {}", discountEntities.size(), sql);
            return discountEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів за фільтром %s в %s. Детально: %s"
                    .formatted(where, DiscountDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Save or update the discountEntity to the database table.
     *
     * @param discountEntity persistent discountEntity
     * @return discountEntity, with identifier of the last added record from the database
     */
    @Override
    public DiscountEntity save(DiscountEntity discountEntity) {
        boolean isNullId = Objects.isNull(discountEntity.getId());
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(
                        isNullId ? SAVE_SQL : UPDATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, discountEntity.getName());
            statement.setString(2, discountEntity.getDescription());
            statement.setShort(3, discountEntity.getValue());

            if (isNullId) {
                LOGGER.info("Формування запиту на збереження.");
            } else {
                statement.setInt(4, discountEntity.getId());

                LOGGER.info("Формування запиту на оновлення.");
            }
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                discountEntity.setId(generatedKeys.getInt(1));
            }

            LOGGER.info("Успішне оновлення або збереження об'єкту: {}", discountEntity);
            return discountEntity;
        } catch (SQLException e) {
            String message = "При збереженні або оновленні запису в %s. Детально: %s"
                    .formatted(DiscountDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Delete a discount from the database table by identifier.
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

    private DiscountDaoImpl() {}

    private static class SingletonHolder {
        public static final DiscountDaoImpl INSTANCE = new DiscountDaoImpl();
    }

    public static DiscountDaoImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
