package com.arakviel.carrepair.persistence.impl;

import static java.util.stream.Collectors.joining;

import com.arakviel.carrepair.persistence.dao.ModelDao;
import com.arakviel.carrepair.persistence.entity.impl.ModelEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.PersistenceException;
import com.arakviel.carrepair.persistence.filter.impl.ModelFilterDto;
import com.arakviel.carrepair.persistence.impl.converter.impl.ModelEntityRowConverter;
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

public class ModelDaoImpl implements ModelDao {

    public static final Logger LOGGER = LoggerFactory.getLogger(ModelDaoImpl.class);

    private static final String FIND_ALL_SQL =
            """
            SELECT id,
                   brand_id,
                   name
              FROM models
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;

    private static final String SAVE_SQL =
            """
            INSERT INTO models(brand_id, name)
            VALUES (?, ?);
            """;

    private static final String UPDATE_SQL =
            """
            UPDATE models
               SET brand_id = ?,
                   name = ?
             WHERE id = ?;
            """;

    private static final String REMOVE_SQL =
            """
            DELETE FROM models
                  WHERE id = ?;
            """;

    /**
     * Get an ModelEntity object by identifier.
     *
     * @param id primary key identifier
     * @return Optional<ModelEntity>
     */
    @Override
    public Optional<ModelEntity> findOneById(Integer id) {
        try (Connection connection = ConnectionManager.get()) {
            return findOneById(id, connection);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(ModelDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new EntityNotFoundException(message);
        }
    }

    /**
     * Get an entity object by identifier with connection.
     *
     * @param id primary key identifier
     * @param connection JDBC Connection
     * @return Optional<ModelEntity>
     */
    @Override
    public Optional<ModelEntity> findOneById(Integer id, Connection connection) {
        try (var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setInt(1, id);
            var resultSet = statement.executeQuery();
            ModelEntity modelEntity = null;
            if (resultSet.next()) {
                modelEntity = ModelEntityRowConverter.getInstance().execute(resultSet);

                LOGGER.info("Founded entity {} by id = {}. Запит: {}", modelEntity, id, FIND_BY_ID_SQL);
            }
            return Optional.ofNullable(modelEntity);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(ModelDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<ModelEntity> findAll() {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = statement.executeQuery();
            var modelEntities = new ArrayList<ModelEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                modelEntities.add(ModelEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", modelEntities.size(), FIND_ALL_SQL);
            return modelEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів в %s. Детально: %s"
                    .formatted(ModelEntity.class.getSimpleName(), e.getMessage());
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
    public List<ModelEntity> findAll(ModelFilterDto filter) {
        var parameters = new ArrayList<>();
        var whereSQL = new ArrayList<String>();

        if (Objects.nonNull(filter.brandEntity())) {
            whereSQL.add("brand_id = ?");
            parameters.add(filter.brandEntity().getId().toString());
        }
        if (Objects.nonNull(filter.name())) {
            whereSQL.add("name LIKE ?");
            parameters.add("%" + filter.name() + "%");
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
            var modelEntities = new ArrayList<ModelEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                modelEntities.add(ModelEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей після фільтрації: {}. Запит: {}", modelEntities.size(), sql);
            return modelEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів за фільтром %s в %s. Детально: %s"
                    .formatted(where, ModelEntity.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Save or update the modelEntity to the database table.
     *
     * @param modelEntity persistent modelEntity
     * @return modelEntity, with identifier of the last added record from the database
     */
    @Override
    public ModelEntity save(ModelEntity modelEntity) {
        boolean isNullId = Objects.isNull(modelEntity.getId());
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(
                        isNullId ? SAVE_SQL : UPDATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, modelEntity.getBrandEntity().getId());
            statement.setString(2, modelEntity.getName());

            if (isNullId) {
                LOGGER.info("Формування запиту на збереження.");
            } else {
                statement.setInt(3, modelEntity.getId());

                LOGGER.info("Формування запиту на оновлення.");
            }
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                modelEntity.setId(generatedKeys.getInt(1));
            }

            LOGGER.info("Успішне оновлення або збереження об'єкту: {}", modelEntity);
            return modelEntity;
        } catch (SQLException e) {
            String message = "При збереженні або оновленні запису в %s. Детально: %s"
                    .formatted(ModelEntity.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Delete an address from the database table by identifier.
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
                    .formatted(ModelDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    private ModelDaoImpl() {}

    private static class SingletonHolder {
        public static final ModelDaoImpl INSTANCE = new ModelDaoImpl();
    }

    public static ModelDaoImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
