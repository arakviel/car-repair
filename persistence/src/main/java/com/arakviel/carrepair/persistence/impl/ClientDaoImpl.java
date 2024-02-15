package com.arakviel.carrepair.persistence.impl;

import static java.util.stream.Collectors.joining;

import com.arakviel.carrepair.persistence.dao.ClientDao;
import com.arakviel.carrepair.persistence.entity.impl.ClientEntity;
import com.arakviel.carrepair.persistence.entity.impl.WorkroomEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.PersistenceException;
import com.arakviel.carrepair.persistence.filter.impl.ClientFilterDto;
import com.arakviel.carrepair.persistence.impl.converter.impl.ClientEntityRowConverter;
import com.arakviel.carrepair.persistence.util.ConnectionManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientDaoImpl implements ClientDao {

    public static final Logger LOGGER = LoggerFactory.getLogger(ClientDaoImpl.class);

    private static final String FIND_ALL_SQL =
            """
            SELECT id,
                   phone,
                   email,
                   first_name,
                   last_name,
                   middle_name,
                   photo,
                   updated_at,
                   created_at
              FROM clients
            """;

    private static final String FIND_ALL_CLIENTS_BY_WORKROOM_SQL =
            """
            SELECT DISTINCT c.id,
                   c.phone,
                   c.email,
                   c.first_name,
                   c.last_name,
                   c.middle_name,
                   c.photo,
                   c.updated_at,
                   c.created_at
              FROM clients AS c
                   JOIN car_repair.orders AS o
                     ON c.id = o.client_id
                   JOIN employee_order AS eo
                     ON o.id = eo.order_id
                   JOIN car_repair.staff AS s
                     ON eo.employee_id = s.id
            WHERE s.workroom_id = ?;
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;
    private static final String SAVE_SQL =
            """
            INSERT INTO clients(id, phone, email, first_name, last_name, middle_name, photo, updated_at, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;
    private static final String UPDATE_SQL =
            """
            UPDATE clients
               SET phone = ?,
                   email = ?,
                   first_name = ?,
                   last_name = ?,
                   middle_name = ?,
                   photo = ?,
                   updated_at = ?,
                   created_at = ?
             WHERE id = ?;
            """;

    private static final String REMOVE_SQL =
            """
            DELETE FROM clients
                  WHERE id = ?;
            """;

    /**
     * Get an ClientEntity object by identifier.
     *
     * @param id primary key identifier
     * @return Optional<ClientEntity>
     */
    @Override
    public Optional<ClientEntity> findOneById(UUID id) {
        try (var connection = ConnectionManager.get()) {
            return findOneById(id, connection);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(ClientDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new EntityNotFoundException(message);
        }
    }

    /**
     * Get an entity object by identifier.
     *
     * @param id primary key identifier
     * @param connection JDBC Connection
     * @return Optional<ClientEntity>
     */
    @Override
    public Optional<ClientEntity> findOneById(UUID id, Connection connection) {
        try (var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setString(1, id.toString());
            var resultSet = statement.executeQuery();
            ClientEntity clientEntity = null;
            if (resultSet.next()) {
                clientEntity = ClientEntityRowConverter.getInstance().execute(resultSet);

                LOGGER.info("Founded entity {} by id = {}. Запит: {}", clientEntity, id, FIND_BY_ID_SQL);
            }
            return Optional.ofNullable(clientEntity);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(ClientDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<ClientEntity> findAll() {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = statement.executeQuery();
            var clientEntities = new ArrayList<ClientEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                clientEntities.add(ClientEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", clientEntities.size(), FIND_ALL_SQL);
            return clientEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів в %s. Детально: %s"
                    .formatted(ClientDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public List<ClientEntity> findAllByWorkroomEntity(WorkroomEntity workroomEntity) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_ALL_CLIENTS_BY_WORKROOM_SQL)) {
            statement.setString(1, workroomEntity.getId().toString());
            var resultSet = statement.executeQuery();
            var clientEntities = new ArrayList<ClientEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                clientEntities.add(ClientEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", clientEntities.size(), FIND_ALL_CLIENTS_BY_WORKROOM_SQL);
            return clientEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів в %s. Детально: %s"
                    .formatted(ClientDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<ClientEntity> findAllByWorkroomEntity(ClientFilterDto filter, WorkroomEntity workroomEntity) {
        return findAll(filter, FIND_ALL_CLIENTS_BY_WORKROOM_SQL, workroomEntity);
    }

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @param filter values for where conditions
     * @return collection of Entities
     */
    @Override
    public List<ClientEntity> findAll(ClientFilterDto filter) {
        return findAll(filter, FIND_ALL_SQL, null);
    }

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @param filter values for where conditions
     * @return collection of Entities
     */
    private List<ClientEntity> findAll(ClientFilterDto filter, String innerSql, WorkroomEntity workroomEntity) {
        var parameters = new ArrayList<>();
        var whereSQL = new ArrayList<String>();

        if (Objects.nonNull(workroomEntity)) {
            parameters.add(workroomEntity.getId().toString());
        }
        if (Objects.nonNull(filter.phone())) {
            whereSQL.add("phone LIKE ?");
            parameters.add("%" + filter.phone() + "%");
        }
        if (Objects.nonNull(filter.email())) {
            whereSQL.add("email LIKE ?");
            parameters.add("%" + filter.email() + "%");
        }
        if (Objects.nonNull(filter.firstName())) {
            whereSQL.add("first_name LIKE ?");
            parameters.add("%" + filter.firstName() + "%");
        }
        if (Objects.nonNull(filter.lastName())) {
            whereSQL.add("last_name LIKE ?");
            parameters.add("%" + filter.lastName() + "%");
        }
        if (Objects.nonNull(filter.middleName())) {
            whereSQL.add("middle_name LIKE ?");
            parameters.add("%" + filter.middleName() + "%");
        }
        if (Objects.nonNull(filter.updatedAt())) {
            whereSQL.add("updated_at = ?");
            parameters.add(filter.updatedAt());
        }
        if (Objects.nonNull(filter.createdAt())) {
            whereSQL.add("created_at = ?");
            parameters.add(filter.createdAt());
        }

        String where = "";
        if (!whereSQL.isEmpty()) {
            where = whereSQL.stream()
                    .collect(joining(" AND ", Objects.isNull(workroomEntity) ? " WHERE " : " AND ", ""));
        }
        var sql = innerSql + where;

        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }
            var resultSet = statement.executeQuery();
            var clientEntities = new ArrayList<ClientEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                clientEntities.add(ClientEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей після фільтрації: {}. Запит: {}", clientEntities.size(), sql);
            return clientEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів за фільтром %s в %s. Детально: %s"
                    .formatted(where, ClientDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Save or update the clientEntity to the database table.
     *
     * @param clientEntity persistent clientEntity
     * @return clientEntity, with identifier of the last added record from the database
     */
    @Override
    public ClientEntity save(ClientEntity clientEntity) {
        boolean isNullId = Objects.isNull(clientEntity.getId());
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(isNullId ? SAVE_SQL : UPDATE_SQL)) {
            if (isNullId) {
                var id = UUID.randomUUID();
                clientEntity.setId(id);
                statement.setString(1, id.toString());
                statement.setString(2, clientEntity.getPhone());
                statement.setString(3, clientEntity.getEmail());
                statement.setString(4, clientEntity.getFirstName());
                statement.setString(5, clientEntity.getLastName());
                statement.setString(6, clientEntity.getMiddleName());
                if (Objects.nonNull(clientEntity.getPhoto())) {
                    statement.setBlob(7, new ByteArrayInputStream(clientEntity.getPhoto()));
                } else {
                    statement.setBytes(7, null);
                }
                statement.setTimestamp(8, Timestamp.valueOf(clientEntity.getUpdatedAt()));
                statement.setTimestamp(9, Timestamp.valueOf(clientEntity.getCreatedAt()));

                LOGGER.info("Формування запиту на збереження.");
            } else {
                statement.setString(1, clientEntity.getPhone());
                statement.setString(2, clientEntity.getEmail());
                statement.setString(3, clientEntity.getFirstName());
                statement.setString(4, clientEntity.getLastName());
                statement.setString(5, clientEntity.getMiddleName());
                if (Objects.nonNull(clientEntity.getPhoto())) {
                    Blob imageBlob = connection.createBlob();
                    try (OutputStream outputStream = imageBlob.setBinaryStream(1)) {
                        outputStream.write(clientEntity.getPhoto());
                    }
                    statement.setBlob(6, imageBlob);
                } else {
                    statement.setBytes(6, null);
                }
                statement.setTimestamp(7, Timestamp.valueOf(clientEntity.getUpdatedAt()));
                statement.setTimestamp(8, Timestamp.valueOf(clientEntity.getCreatedAt()));
                statement.setString(9, clientEntity.getId().toString());

                LOGGER.info("Формування запиту на оновлення.");
            }
            statement.executeUpdate();

            LOGGER.info("Успішне оновлення або збереження об'єкту: {}", clientEntity);
            return clientEntity;
        } catch (SQLException e) {
            String message = "При збереженні або оновленні запису в %s. Детально: %s"
                    .formatted(ClientDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        } catch (IOException ex) {
            String message = "При збереженні або оновленні запису в %s. Детально: %s"
                    .formatted(ClientDaoImpl.class.getSimpleName(), ex.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Delete a client from the database table by identifier.
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
                    .formatted(ClientDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    private ClientDaoImpl() {}

    private static class SingletonHolder {

        public static final ClientDaoImpl INSTANCE = new ClientDaoImpl();
    }

    public static ClientDaoImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
