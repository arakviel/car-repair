package com.arakviel.carrepair.persistence.impl;

import static java.util.stream.Collectors.joining;

import com.arakviel.carrepair.persistence.dao.PhoneDao;
import com.arakviel.carrepair.persistence.entity.impl.PhoneEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.PersistenceException;
import com.arakviel.carrepair.persistence.filter.impl.PhoneFilterDto;
import com.arakviel.carrepair.persistence.impl.converter.impl.PhoneEntityRowConverter;
import com.arakviel.carrepair.persistence.util.ConnectionManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhoneDaoImpl implements PhoneDao {

    public static final Logger LOGGER = LoggerFactory.getLogger(PhoneDaoImpl.class);

    private static final String FIND_ALL_SQL =
            """
            SELECT id,
                   employee_id,
                   type,
                   value
              FROM phones
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;
    private static final String SAVE_SQL =
            """
            INSERT INTO phones(id, employee_id, type, value)
            VALUES (?, ?, ?, ?);
            """;
    private static final String UPDATE_SQL =
            """
            UPDATE phones
               SET employee_id = ?,
                   type = ?,
                   value = ?
             WHERE id = ?;
            """;

    private static final String REMOVE_SQL =
            """
            DELETE FROM phones
                  WHERE id = ?;
            """;

    /**
     * Get an PhoneEntity object by identifier.
     *
     * @param id primary key identifier
     * @return Optional<PhoneEntity>
     */
    @Override
    public Optional<PhoneEntity> findOneById(UUID id) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setString(1, id.toString());
            var resultSet = statement.executeQuery();
            PhoneEntity phoneEntity = null;
            if (resultSet.next()) {
                phoneEntity = PhoneEntityRowConverter.getInstance().execute(resultSet);

                LOGGER.info("Founded entity %s by id = %s. Запит: %s"
                        .formatted(phoneEntity, id.toString(), FIND_BY_ID_SQL));
            }
            return Optional.ofNullable(phoneEntity);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(PhoneDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<PhoneEntity> findAll() {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = statement.executeQuery();
            var phoneEntities = new ArrayList<PhoneEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                phoneEntities.add(PhoneEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", phoneEntities.size(), FIND_ALL_SQL);
            return phoneEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів в %s. Детально: %s"
                    .formatted(PhoneDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<PhoneEntity> findAll(PhoneFilterDto filter) {
        var parameters = new ArrayList<>();
        var whereSQL = new ArrayList<String>();

        if (Objects.nonNull(filter.employeeEntity())) {
            whereSQL.add("employee_id = ?");
            parameters.add(filter.employeeEntity().getId());
        }
        if (Objects.nonNull(filter.type())) {
            whereSQL.add("type = ?");
            parameters.add(filter.type());
        }
        if (Objects.nonNull(filter.value())) {
            whereSQL.add("value LINE ?");
            parameters.add("%" + filter.value() + "%");
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
            var phoneEntities = new ArrayList<PhoneEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                phoneEntities.add(PhoneEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей після фільтрації: {}. Запит: {}", phoneEntities.size(), sql);
            return phoneEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів за фільтром %s в %s. Детально: %s"
                    .formatted(where, PhoneDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Save or update the phone to the database table.
     *
     * @param phoneEntity persistent phone
     * @return phone, with identifier of the last added record from the database
     */
    @Override
    public PhoneEntity save(PhoneEntity phoneEntity) {
        boolean isNullId = Objects.isNull(phoneEntity.getId());
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(isNullId ? SAVE_SQL : UPDATE_SQL)) {
            if (isNullId) {
                var id = UUID.randomUUID();
                phoneEntity.setId(id);
                statement.setString(1, id.toString());
                statement.setString(2, phoneEntity.getEmployeeEntity().getId().toString());
                statement.setString(3, phoneEntity.getPhoneType().getType());
                statement.setString(4, phoneEntity.getValue());

                LOGGER.info("Формування запиту на збереження.");
            } else {
                statement.setString(1, phoneEntity.getEmployeeEntity().getId().toString());
                statement.setString(2, phoneEntity.getPhoneType().getType());
                statement.setString(3, phoneEntity.getValue());
                statement.setString(4, phoneEntity.getId().toString());

                LOGGER.info("Формування запиту на оновлення.");
            }
            statement.executeUpdate();

            LOGGER.info("Успішне оновлення або збереження об'єкту: {}", phoneEntity);
            return phoneEntity;
        } catch (SQLException e) {
            String message = "При збереженні або оновленні запису в %s. Детально: %s"
                    .formatted(PhoneDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Delete a phone from the database table by identifier.
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
                    .formatted(PhoneDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    private PhoneDaoImpl() {}

    private static class SingletonHolder {
        public static final PhoneDaoImpl INSTANCE = new PhoneDaoImpl();
    }

    public static PhoneDaoImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
