package com.arakviel.carrepair.persistence.impl;

import static java.util.stream.Collectors.joining;

import com.arakviel.carrepair.persistence.dao.WorkroomDao;
import com.arakviel.carrepair.persistence.entity.impl.WorkroomEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.PersistenceException;
import com.arakviel.carrepair.persistence.filter.impl.WorkroomFilterDto;
import com.arakviel.carrepair.persistence.impl.converter.impl.WorkroomEntityRowConverter;
import com.arakviel.carrepair.persistence.util.ConnectionManager;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkroomDaoImpl implements WorkroomDao {

    public static final Logger LOGGER = LoggerFactory.getLogger(WorkroomDaoImpl.class);

    private static final String FIND_ALL_SQL =
            """
            SELECT id,
                   address_id,
                   name,
                   photo,
                   description
              FROM workrooms
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;
    private static final String SAVE_SQL =
            """
            INSERT INTO workrooms(id, address_id, name, photo, description)
            VALUES (?, ?, ?, ?, ?);
            """;
    private static final String UPDATE_SQL =
            """
            UPDATE workrooms
               SET address_id = ?,
                   name = ?,
                   photo = ?,
                   description = ?
             WHERE id = ?;
            """;

    private static final String REMOVE_SQL =
            """
            DELETE FROM workrooms
                  WHERE id = ?;
            """;

    /**
     * Get an WorkroomEntity object by identifier.
     *
     * @param id primary key identifier
     * @return Optional<WorkroomEntity>
     */
    @Override
    public Optional<WorkroomEntity> findOneById(UUID id) {
        try (var connection = ConnectionManager.get()) {
            return findOneById(id, connection);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(WorkroomDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new EntityNotFoundException(message);
        }
    }

    /**
     * Get an entity object by identifier.
     *
     * @param id primary key identifier
     * @param connection JDBC Connection
     * @return Optional<WorkroomEntity>
     */
    @Override
    public Optional<WorkroomEntity> findOneById(UUID id, Connection connection) {
        try (var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setString(1, id.toString());
            var resultSet = statement.executeQuery();
            WorkroomEntity workroomEntity = null;
            if (resultSet.next()) {
                workroomEntity = WorkroomEntityRowConverter.getInstance().execute(resultSet);

                LOGGER.info("Founded entity %s by id = %s. Запит: %s"
                        .formatted(workroomEntity, id.toString(), FIND_BY_ID_SQL));
            }
            return Optional.ofNullable(workroomEntity);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(WorkroomDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<WorkroomEntity> findAll() {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = statement.executeQuery();
            var workroomEntities = new ArrayList<WorkroomEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                workroomEntities.add(WorkroomEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", workroomEntities.size(), FIND_ALL_SQL);
            return workroomEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів в %s. Детально: %s"
                    .formatted(AddressDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<WorkroomEntity> findAll(WorkroomFilterDto filter) {
        var parameters = new ArrayList<>();
        var whereSQL = new ArrayList<String>();

        if (Objects.nonNull(filter.addressEntity())) {
            whereSQL.add("address_id = ?");
            parameters.add(filter.addressEntity().getId().toString());
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
            var workroomEntities = new ArrayList<WorkroomEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                workroomEntities.add(WorkroomEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей після фільтрації: {}. Запит: {}", workroomEntities.size(), sql);
            return workroomEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів за фільтром %s в %s. Детально: %s"
                    .formatted(where, WorkroomDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Save or update the workroom to the database table.
     *
     * @param workroomEntity persistent workroom
     * @return workroom, with identifier of the last added record from the database
     */
    @Override
    public WorkroomEntity save(WorkroomEntity workroomEntity) {
        boolean isNullId = Objects.isNull(workroomEntity.getId());
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(isNullId ? SAVE_SQL : UPDATE_SQL)) {
            if (isNullId) {
                var id = UUID.randomUUID();
                workroomEntity.setId(id);
                statement.setString(1, id.toString());
                statement.setString(2, workroomEntity.getAddressEntity().getId().toString());
                statement.setString(3, workroomEntity.getName());
                statement.setBlob(4, new ByteArrayInputStream(workroomEntity.getPhoto()));
                statement.setString(5, workroomEntity.getDescription());

                LOGGER.info("Формування запиту на збереження.");
            } else {
                statement.setString(1, workroomEntity.getAddressEntity().getId().toString());
                statement.setString(2, workroomEntity.getName());
                statement.setBlob(3, new ByteArrayInputStream(workroomEntity.getPhoto()));
                statement.setString(4, workroomEntity.getDescription());
                statement.setString(5, workroomEntity.getId().toString());

                LOGGER.info("Формування запиту на оновлення.");
            }
            statement.executeUpdate();

            LOGGER.info("Успішне оновлення або збереження об'єкту: {}", workroomEntity);
            return workroomEntity;
        } catch (SQLException e) {
            String message = "При збереженні або оновленні запису в %s. Детально: %s"
                    .formatted(WorkroomDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Delete a workroom from the database table by identifier.
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
                    .formatted(WorkroomDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    private WorkroomDaoImpl() {}

    private static class SingletonHolder {
        public static final WorkroomDaoImpl INSTANCE = new WorkroomDaoImpl();
    }

    public static WorkroomDaoImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
