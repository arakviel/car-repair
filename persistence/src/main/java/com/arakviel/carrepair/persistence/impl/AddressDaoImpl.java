package com.arakviel.carrepair.persistence.impl;

import static java.util.stream.Collectors.joining;

import com.arakviel.carrepair.persistence.dao.AddressDao;
import com.arakviel.carrepair.persistence.entity.impl.AddressEntity;
import com.arakviel.carrepair.persistence.entity.impl.WorkroomEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.PersistenceException;
import com.arakviel.carrepair.persistence.filter.impl.AddressFilterDto;
import com.arakviel.carrepair.persistence.impl.converter.impl.AddressEntityRowConverter;
import com.arakviel.carrepair.persistence.util.ConnectionManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddressDaoImpl implements AddressDao {

    public static final Logger LOGGER = LoggerFactory.getLogger(AddressDaoImpl.class);

    private static final String FIND_ALL_SQL =
            """
            SELECT id,
                   country,
                   region,
                   city,
                   street,
                   home
              FROM addresses
            """;

    private static final String FIND_ALL_BY_WORKROOM =
            """
            SELECT DISTINCT a.id,
                            a.country,
                            a.region,
                            a.city,
                            a.street,
                            a.home
              FROM addresses AS a
                   JOIN car_repair.staff AS s
                     ON a.id = s.address_id
             WHERE s.workroom_id = ?;
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;
    private static final String SAVE_SQL =
            """
            INSERT INTO addresses(id, country, region, city, street, home)
            VALUES (?, ?, ?, ?, ?, ?);
            """;
    private static final String UPDATE_SQL =
            """
            UPDATE addresses
               SET country = ?,
                   region = ?,
                   city = ?,
                   street = ?,
                   home = ?
             WHERE id = ?;
            """;

    private static final String REMOVE_SQL =
            """
            DELETE FROM addresses
                  WHERE id = ?;
            """;

    /**
     * Get an address object by identifier.
     *
     * @param id primary key identifier
     * @return Optional<AddressEntity>
     */
    @Override
    public Optional<AddressEntity> findOneById(UUID id) {
        try (var connection = ConnectionManager.get()) {
            return findOneById(id, connection);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(AddressDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new EntityNotFoundException(message);
        }
    }

    /**
     * Get an entity object by identifier with connection.
     *
     * @param id primary key identifier
     * @param connection JDBC Connection
     * @return Optional<AddressEntity>
     */
    @Override
    public Optional<AddressEntity> findOneById(UUID id, Connection connection) {
        try (var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setString(1, id.toString());
            var resultSet = statement.executeQuery();
            AddressEntity addressEntity = null;
            if (resultSet.next()) {
                addressEntity = AddressEntityRowConverter.getInstance().execute(resultSet);

                LOGGER.info("Founded entity {} by id = {}. Запит: {}", addressEntity, id, FIND_BY_ID_SQL);
            }
            return Optional.ofNullable(addressEntity);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(AddressDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<AddressEntity> findAll() {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = statement.executeQuery();
            var addressEntities = new ArrayList<AddressEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                addressEntities.add(AddressEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", addressEntities.size(), FIND_ALL_SQL);
            return addressEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів в %s. Детально: %s"
                    .formatted(AddressDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public List<AddressEntity> findAllByWorkroomEntity(WorkroomEntity workroomEntity) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_ALL_BY_WORKROOM)) {
            statement.setString(1, workroomEntity.getId().toString());
            var resultSet = statement.executeQuery();
            var addressEntities = new ArrayList<AddressEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                addressEntities.add(AddressEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", addressEntities.size(), FIND_ALL_BY_WORKROOM);
            return addressEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів в %s. Детально: %s"
                    .formatted(AddressDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public List<AddressEntity> findAllByWorkroomEntity(AddressFilterDto filter, WorkroomEntity workroomEntity) {
        return findAll(filter, FIND_ALL_BY_WORKROOM, workroomEntity);
    }

    @Override
    public List<AddressEntity> findAll(AddressFilterDto filter) {
        return findAll(filter, FIND_ALL_SQL, null);
    }

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @param filter values for where conditions
     * @return collection of Entities
     */
    private List<AddressEntity> findAll(AddressFilterDto filter, String innerSql, WorkroomEntity workroomEntity) {
        var parameters = new ArrayList<>();
        var whereSQL = new ArrayList<String>();

        if (Objects.nonNull(workroomEntity)) {
            parameters.add(workroomEntity.getId().toString());
        }
        if (Objects.nonNull(filter.country())) {
            whereSQL.add("country LIKE ?");
            parameters.add("%" + filter.country() + "%");
        }
        if (Objects.nonNull(filter.region())) {
            whereSQL.add("region LIKE ?");
            parameters.add("%" + filter.region() + "%");
        }
        if (Objects.nonNull(filter.city())) {
            whereSQL.add("city LIKE ?");
            parameters.add("%" + filter.city() + "%");
        }
        if (Objects.nonNull(filter.street())) {
            whereSQL.add("street LIKE ?");
            parameters.add("%" + filter.street() + "%");
        }
        if (Objects.nonNull(filter.home())) {
            whereSQL.add("home LIKE ?");
            parameters.add("%" + filter.home() + "%");
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
            var addressEntities = new ArrayList<AddressEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                addressEntities.add(AddressEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей після фільтрації: {}. Запит: {}", addressEntities.size(), sql);
            return addressEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів за фільтром %s в %s. Детально: %s"
                    .formatted(where, AddressDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Save or update the addressEntity to the database table.
     *
     * @param addressEntity persistent addressEntity
     * @return addressEntity, with identifier of the last added record from the database
     */
    @Override
    public AddressEntity save(AddressEntity addressEntity) {
        boolean isNullId = Objects.isNull(addressEntity.getId());
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(isNullId ? SAVE_SQL : UPDATE_SQL)) {
            if (isNullId) {
                var id = UUID.randomUUID();
                addressEntity.setId(id);
                statement.setString(1, id.toString());
                statement.setString(2, addressEntity.getCountry());
                statement.setString(3, addressEntity.getRegion());
                statement.setString(4, addressEntity.getCity());
                statement.setString(5, addressEntity.getStreet());
                statement.setString(6, addressEntity.getHome());

                LOGGER.info("Формування запиту на збереження.");
            } else {
                statement.setString(1, addressEntity.getCountry());
                statement.setString(2, addressEntity.getRegion());
                statement.setString(3, addressEntity.getCity());
                statement.setString(4, addressEntity.getStreet());
                statement.setString(5, addressEntity.getHome());
                statement.setString(6, addressEntity.getId().toString());

                LOGGER.info("Формування запиту на оновлення.");
            }
            statement.executeUpdate();

            LOGGER.info("Успішне оновлення або збереження об'єкту: {}", addressEntity);
            return addressEntity;
        } catch (SQLException e) {
            String message = "При збереженні або оновленні запису в %s. Детально: %s"
                    .formatted(AddressDaoImpl.class.getSimpleName(), e.getMessage());
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
    public void remove(UUID id) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(REMOVE_SQL)) {
            statement.setString(1, id.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            String message = "Помилка при операції видалення рядка таблиці в %s. Детально: %s"
                    .formatted(AddressDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    private AddressDaoImpl() {}

    private static class SingletonHolder {
        public static final AddressDaoImpl INSTANCE = new AddressDaoImpl();
    }

    public static AddressDaoImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
