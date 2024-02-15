package com.arakviel.carrepair.persistence.impl;

import static java.util.stream.Collectors.joining;

import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.dao.EmployeeDao;
import com.arakviel.carrepair.persistence.entity.impl.EmployeeEntity;
import com.arakviel.carrepair.persistence.entity.impl.OrderEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.PersistenceException;
import com.arakviel.carrepair.persistence.filter.impl.EmployeeFilterDto;
import com.arakviel.carrepair.persistence.impl.converter.impl.EmployeeEntityRowConverter;
import com.arakviel.carrepair.persistence.impl.converter.impl.OrderEntityRowConverter;
import com.arakviel.carrepair.persistence.util.ConnectionManager;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmployeeDaoImpl implements EmployeeDao {

    public static final Logger LOGGER = LoggerFactory.getLogger(EmployeeDaoImpl.class);

    private static final String FIND_ALL_SQL =
            """
            SELECT id,
                   address_id,
                   workroom_id,
                   position_id,
                   first_name,
                   last_name,
                   middle_name,
                   photo,
                   passport_doc_copy,
                   bank_number_doc_copy,
                   other_doc_copy,
                   updated_at,
                   created_at
              FROM staff
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;
    private static final String SAVE_SQL =
            """
            INSERT INTO staff(id, address_id, workroom_id, position_id, first_name, last_name, middle_name, photo, passport_doc_copy, bank_number_doc_copy, other_doc_copy, updated_at, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;
    private static final String UPDATE_SQL =
            """
            UPDATE staff
               SET address_id = ?,
                   workroom_id = ?,
                   position_id = ?,
                   first_name = ?,
                   last_name = ?,
                   middle_name = ?,
                   photo = ?,
                   passport_doc_copy = ?,
                   bank_number_doc_copy = ?,
                   other_doc_copy = ?,
                   updated_at = ?,
                   created_at = ?
             WHERE id = ?;
            """;

    private static final String REMOVE_SQL =
            """
            DELETE FROM staff
                  WHERE id = ?;
            """;

    private static final String FIND_ALL_ORDERS_IN_EMPLOYEE_SQL =
            """
            SELECT o.id,
                   o.client_id,
                   o.car_id,
                   o.discount_id,
                   o.price_whole_part,
                   o.price_decimal_part,
                   o.payment_at,
                   o.updated_at,
                   o.created_at
              FROM orders AS o
                   JOIN employee_order AS eo
                     ON o.id = eo.order_id
             WHERE eo.employee_id = ?;
            """;

    /**
     * Get an SpareEntity object by identifier.
     *
     * @param id primary key identifier
     * @return Optional<SpareEntity>
     */
    @Override
    public Optional<EmployeeEntity> findOneById(UUID id) {
        try (var connection = ConnectionManager.get()) {
            return findOneById(id, connection);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(EmployeeDaoImpl.class.getSimpleName(), e.getMessage());
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
    public Optional<EmployeeEntity> findOneById(UUID id, Connection connection) {
        try (var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setString(1, id.toString());
            var resultSet = statement.executeQuery();
            EmployeeEntity employeeEntity = null;
            if (resultSet.next()) {
                employeeEntity = EmployeeEntityRowConverter.getInstance().execute(resultSet);

                LOGGER.info("Founded entity %s by id = %s. Запит: %s"
                        .formatted(employeeEntity, id.toString(), FIND_BY_ID_SQL));
            }
            return Optional.ofNullable(employeeEntity);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(EmployeeDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<EmployeeEntity> findAll() {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = statement.executeQuery();
            var employeeEntities = new ArrayList<EmployeeEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                employeeEntities.add(EmployeeEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", employeeEntities.size(), FIND_ALL_SQL);
            return employeeEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів в %s. Детально: %s"
                    .formatted(SpareDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<EmployeeEntity> findAll(EmployeeFilterDto filter) {
        var parameters = new ArrayList<>();
        var whereSQL = new ArrayList<String>();

        if (Objects.nonNull(filter.addressEntity())) {
            whereSQL.add("address_id = ?");
            parameters.add(filter.addressEntity().getId().toString());
        }
        if (Objects.nonNull(filter.workroomEntity())) {
            whereSQL.add("workroom_id = ?");
            parameters.add(filter.workroomEntity().getId().toString());
        }
        if (Objects.nonNull(filter.positionEntity())) {
            whereSQL.add("position_id = ?");
            parameters.add(filter.positionEntity().getId());
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
            where = whereSQL.stream().collect(joining(" AND ", " WHERE ", ""));
        }
        var sql = FIND_ALL_SQL + where;

        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }
            var resultSet = statement.executeQuery();
            var employeeEntities = new ArrayList<EmployeeEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                employeeEntities.add(EmployeeEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей після фільтрації: {}. Запит: {}", employeeEntities.size(), sql);
            return employeeEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів за фільтром %s в %s. Детально: %s"
                    .formatted(where, EmployeeDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Save or update the employeeEntity to the database table.
     *
     * @param employeeEntity persistent spare
     * @return employeeEntity, with identifier of the last added record from the database
     */
    @Override
    public EmployeeEntity save(EmployeeEntity employeeEntity) {
        boolean isNullId = Objects.isNull(employeeEntity.getId());
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(isNullId ? SAVE_SQL : UPDATE_SQL)) {
            byte[] bankNumberDocCopy = Objects.nonNull(employeeEntity.getBankNumberDocCopy())
                    ? employeeEntity.getBankNumberDocCopy()
                    : new byte[] {};
            byte[] otherDocCopy = Objects.nonNull(employeeEntity.getOtherDocCopy())
                    ? employeeEntity.getOtherDocCopy()
                    : new byte[] {};
            if (isNullId) {
                var id = UUID.randomUUID();
                employeeEntity.setId(id);
                statement.setString(1, id.toString());
                statement.setString(2, employeeEntity.getAddressEntity().getId().toString());
                statement.setString(
                        3, employeeEntity.getWorkroomEntity().getId().toString());
                statement.setInt(4, employeeEntity.getPositionEntity().getId());
                statement.setString(5, employeeEntity.getFirstName());
                statement.setString(6, employeeEntity.getLastName());
                statement.setString(7, employeeEntity.getMiddleName());
                statement.setBlob(8, new ByteArrayInputStream(employeeEntity.getPhoto()));
                statement.setBlob(9, new ByteArrayInputStream(employeeEntity.getPassportDocCopy()));
                statement.setBlob(10, new ByteArrayInputStream(bankNumberDocCopy));
                statement.setBlob(11, new ByteArrayInputStream(otherDocCopy));
                statement.setTimestamp(12, Timestamp.valueOf(employeeEntity.getUpdatedAt()));
                statement.setTimestamp(13, Timestamp.valueOf(employeeEntity.getCreatedAt()));

                LOGGER.info("Формування запиту на збереження.");
            } else {
                statement.setString(1, employeeEntity.getAddressEntity().getId().toString());
                statement.setString(
                        2, employeeEntity.getWorkroomEntity().getId().toString());
                statement.setInt(3, employeeEntity.getPositionEntity().getId());
                statement.setString(4, employeeEntity.getFirstName());
                statement.setString(5, employeeEntity.getLastName());
                statement.setString(6, employeeEntity.getMiddleName());
                statement.setBlob(7, new ByteArrayInputStream(employeeEntity.getPhoto()));
                statement.setBlob(8, new ByteArrayInputStream(employeeEntity.getPassportDocCopy()));
                statement.setBlob(9, new ByteArrayInputStream(bankNumberDocCopy));
                statement.setBlob(10, new ByteArrayInputStream(otherDocCopy));
                statement.setTimestamp(11, Timestamp.valueOf(employeeEntity.getUpdatedAt()));
                statement.setTimestamp(12, Timestamp.valueOf(employeeEntity.getCreatedAt()));
                statement.setString(13, employeeEntity.getId().toString());

                LOGGER.info("Формування запиту на оновлення.");
            }
            statement.executeUpdate();

            // Скидання userEntity, якщо він не порожній
            /*            if (Objects.nonNull(employeeEntity.getUserEntity())) {
                employeeEntity.setUserEntity(null);
            }*/

            LOGGER.info("Успішне оновлення або збереження об'єкту: {}", employeeEntity);
            return employeeEntity;
        } catch (SQLException e) {
            String message = "При збереженні або оновленні запису в %s. Детально: %s"
                    .formatted(EmployeeDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Delete an employee from the database table by identifier.
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
                    .formatted(EmployeeDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public List<OrderEntity> findAllOrders(UUID employeeId) {
        try (Connection connection = ConnectionManager.get();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL_ORDERS_IN_EMPLOYEE_SQL)) {
            statement.setString(1, employeeId.toString());
            ResultSet resultSet = statement.executeQuery();
            var orderEntities = new ArrayList<OrderEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                orderEntities.add(OrderEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", orderEntities.size(), FIND_ALL_ORDERS_IN_EMPLOYEE_SQL);
            return orderEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів order в %s. Детально: %s"
                    .formatted(EmployeeDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public void attachToOrder(UUID employeeId, UUID orderId) {
        DaoFactory.getInstance().getOrderDao().attachToEmployee(orderId, employeeId);
    }

    @Override
    public void detachFromOrder(UUID employeeId, UUID orderId) {
        DaoFactory.getInstance().getOrderDao().detachFromEmployee(orderId, employeeId);
    }

    private EmployeeDaoImpl() {}

    private static class SingletonHolder {
        public static final EmployeeDaoImpl INSTANCE = new EmployeeDaoImpl();
    }

    public static EmployeeDaoImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
