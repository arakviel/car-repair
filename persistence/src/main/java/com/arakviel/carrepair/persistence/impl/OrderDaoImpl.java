package com.arakviel.carrepair.persistence.impl;

import static java.util.stream.Collectors.joining;

import com.arakviel.carrepair.persistence.dao.OrderDao;
import com.arakviel.carrepair.persistence.entity.impl.EmployeeEntity;
import com.arakviel.carrepair.persistence.entity.impl.OrderEntity;
import com.arakviel.carrepair.persistence.entity.impl.ServiceEntity;
import com.arakviel.carrepair.persistence.entity.impl.SpareEntity;
import com.arakviel.carrepair.persistence.entity.impl.WorkroomEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.PersistenceException;
import com.arakviel.carrepair.persistence.filter.impl.OrderFilterDto;
import com.arakviel.carrepair.persistence.impl.converter.impl.EmployeeEntityRowConverter;
import com.arakviel.carrepair.persistence.impl.converter.impl.OrderEntityRowConverter;
import com.arakviel.carrepair.persistence.impl.converter.impl.ServiceEntityRowConverter;
import com.arakviel.carrepair.persistence.impl.converter.impl.SpareEntityRowConverter;
import com.arakviel.carrepair.persistence.util.ConnectionManager;
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

public class OrderDaoImpl implements OrderDao {

    public static final Logger LOGGER = LoggerFactory.getLogger(OrderDaoImpl.class);

    private static final String FIND_ALL_SQL =
            """
            SELECT id,
                   client_id,
                   car_id,
                   discount_id,
                   price_whole_part,
                   price_decimal_part,
                   payment_type,
                   payment_at,
                   updated_at,
                   created_at
              FROM orders
            """;
    private static final String FIND_ALL_ORDERS_BY_WORKROOM_SQL =
            """
            SELECT DISTINCT o.id,
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
             WHERE eo.employee_id IN (SELECT id
                                        FROM staff
                                       WHERE workroom_id = ?);
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;
    private static final String SAVE_SQL =
            """
            INSERT INTO orders(id, client_id, car_id, discount_id, price_whole_part, price_decimal_part, payment_type, payment_at, updated_at, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;
    private static final String UPDATE_SQL =
            """
            UPDATE orders
               SET client_id = ?,
                   car_id = ?,
                   discount_id = ?,
                   price_whole_part = ?,
                   price_decimal_part = ?,
                   payment_type = ?,
                   payment_at = ?,
                   updated_at = ?,
                   created_at = ?
             WHERE id = ?;
            """;
    private static final String REMOVE_SQL =
            """
            DELETE FROM orders
                  WHERE id = ?;
            """;

    private static final String FIND_ALL_STAFF_IN_ORDER_SQL =
            """
            SELECT e.id,
                   e.address_id,
                   e.workroom_id,
                   e.position_id,
                   e.first_name,
                   e.last_name,
                   e.middle_name,
                   e.photo,
                   e.passport_doc_copy,
                   e.bank_number_doc_copy,
                   e.other_doc_copy,
                   e.updated_at,
                   e.created_at
              FROM staff AS e
                   JOIN employee_order AS eo
                     ON e.id = eo.employee_id
             WHERE eo.order_id = ?;
            """;
    private static final String SAVE_EMPLOYEE_IN_ORDER_SQL =
            """
            INSERT INTO employee_order(order_id, employee_id)
            VALUES (?, ?);
            """;
    private static final String REMOVE_EMPLOYEE_FROM_ORDER_SQL =
            """
            DELETE FROM employee_order
                  WHERE order_id = ? AND employee_id = ?;
            """;
    private static final String FIND_ALL_SERVICES_IN_ORDER_SQL =
            """
            SELECT s.id,
                   s.name,
                   s.description,
                   s.photo,
                   s.currency_id,
                   s.price_whole_part,
                   s.price_decimal_part,
                   so.description AS extra_description
              FROM services AS s
                   JOIN service_order AS so
                     ON s.id = so.service_id
             WHERE so.order_id = ?;
            """;
    // ???
    private static final String FIND_DESCRIPTION_SERVICE_ORDER =
            """
            SELECT description
              FROM service_order
             WHERE order_id = ? AND service_id = ?
            """;
    private static final String SAVE_SERVICE_IN_ORDER_SQL =
            """
            INSERT INTO service_order(order_id, service_id, description)
            VALUES (?, ?, ?);
            """;
    private static final String REMOVE_SERVICE_FROM_ORDER_SQL =
            """
            DELETE FROM service_order
                  WHERE order_id = ? AND service_id = ?;
            """;
    private static final String FIND_ALL_SPARES_IN_ORDER_SQL =
            """
            SELECT s.id,
                   s.workroom_id,
                   s.name,
                   s.description,
                   s.photo,
                   s.price_whole_part,
                   s.price_decimal_part,
                   s.quantity_in_stock,
                   so.quantity AS extra_quantity
              FROM spares AS s
                   JOIN spare_order AS so
                     ON s.id = so.spare_id
             WHERE so.order_id = ?;
            """;
    // ???
    private static final String FIND_QUANTITY_SPARE_ORDER =
            """
            SELECT quantity
              FROM spare_order
             WHERE order_id = ? AND spare_id = ?
            """;

    private static final String SAVE_SPARE_IN_ORDER_SQL =
            """
            INSERT INTO spare_order(order_id, spare_id, quantity)
            VALUES (?, ?, ?);
            """;
    private static final String REMOVE_SPARE_FROM_ORDER_SQL =
            """
            DELETE FROM spare_order
                  WHERE order_id = ? AND spare_id = ?;
            """;

    /**
     * Get an OrderEntity object by identifier.
     *
     * @param id primary key identifier
     * @return Optional<OrderEntity>
     */
    @Override
    public Optional<OrderEntity> findOneById(UUID id) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setString(1, id.toString());
            var resultSet = statement.executeQuery();
            OrderEntity employeeEntity = null;
            if (resultSet.next()) {
                employeeEntity = OrderEntityRowConverter.getInstance().execute(resultSet);

                LOGGER.info("Founded entity %s by id = %s. Запит: %s"
                        .formatted(employeeEntity, id.toString(), FIND_BY_ID_SQL));
            }
            return Optional.ofNullable(employeeEntity);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(OrderDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<OrderEntity> findAll() {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = statement.executeQuery();
            var orderEntities = new ArrayList<OrderEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                orderEntities.add(OrderEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", orderEntities.size(), FIND_ALL_SQL);
            return orderEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів в %s. Детально: %s"
                    .formatted(OrderDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public List<OrderEntity> findAllByWorkroomEntity(WorkroomEntity workroomEntity) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_ALL_ORDERS_BY_WORKROOM_SQL)) {
            statement.setString(1, workroomEntity.getId().toString());
            var resultSet = statement.executeQuery();
            var orderEntities = new ArrayList<OrderEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                orderEntities.add(OrderEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", orderEntities.size(), FIND_ALL_ORDERS_BY_WORKROOM_SQL);
            return orderEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів в %s. Детально: %s"
                    .formatted(OrderDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public List<OrderEntity> findAllByWorkroomEntity(OrderFilterDto filter, WorkroomEntity workroomEntity) {
        return findAll(filter, FIND_ALL_ORDERS_BY_WORKROOM_SQL, workroomEntity);
    }

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @param filter values for where conditions
     * @return collection of Entities
     */
    @Override
    public List<OrderEntity> findAll(OrderFilterDto filter) {
        return findAll(filter, FIND_ALL_SQL, null);
    }

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @param filter values for where conditions
     * @return collection of Entities
     */
    private List<OrderEntity> findAll(OrderFilterDto filter, String innerSql, WorkroomEntity workroomEntity) {
        var parameters = new ArrayList<>();
        var whereSQL = new ArrayList<String>();

        if (Objects.nonNull(workroomEntity)) {
            parameters.add(workroomEntity.getId().toString());
        }
        if (Objects.nonNull(filter.clientEntity())) {
            whereSQL.add("client_id = ?");
            parameters.add(filter.clientEntity().getId());
        }
        if (Objects.nonNull(filter.carEntity())) {
            whereSQL.add("car_id = ?");
            parameters.add(filter.carEntity().getId());
        }
        if (Objects.nonNull(filter.discountEntity())) {
            whereSQL.add("discount_id = ?");
            parameters.add(filter.discountEntity().getId());
        }
        if (Objects.nonNull(filter.price())) {
            whereSQL.add("price_whole_part = ?");
            parameters.add(filter.price().wholePart());
        }
        if (Objects.nonNull(filter.paymentType())) {
            whereSQL.add("payment_type LIKE ?");
            parameters.add("%" + filter.paymentType() + "%");
        }
        if (Objects.nonNull(filter.paymentAt())) {
            whereSQL.add("payment_at = ?");
            parameters.add(filter.paymentAt());
        }
        if (Objects.nonNull(filter.updatedAt())) {
            whereSQL.add("updated_at = ?");
            parameters.add(filter.updatedAt());
        }
        if (Objects.nonNull(filter.createdAt())) {
            whereSQL.add("created_at = ?");
            parameters.add(filter.createdAt());
        }

        var where =
                whereSQL.stream().collect(joining(" AND ", Objects.isNull(workroomEntity) ? " WHERE " : " AND ", ""));
        var sql = innerSql + where;

        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }
            var resultSet = statement.executeQuery();
            var orderEntities = new ArrayList<OrderEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                orderEntities.add(OrderEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей після фільтрації: {}. Запит: {}", orderEntities.size(), sql);
            return orderEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів за фільтром %s в %s. Детально: %s"
                    .formatted(where, OrderDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Save or update the orderEntity to the database table.
     *
     * @param orderEntity persistent spare
     * @return orderEntity, with identifier of the last added record from the database
     */
    @Override
    public OrderEntity save(OrderEntity orderEntity) {
        boolean isNullId = Objects.isNull(orderEntity.getId());
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(isNullId ? SAVE_SQL : UPDATE_SQL)) {
            if (isNullId) {
                var id = UUID.randomUUID();
                orderEntity.setId(id);
                statement.setString(1, id.toString());
                statement.setString(2, orderEntity.getClientEntity().getId().toString());
                statement.setString(3, orderEntity.getCarEntity().getId().toString());
                statement.setInt(4, orderEntity.getDiscountEntity().getId());
                statement.setInt(5, orderEntity.getPrice().wholePart());
                statement.setInt(6, orderEntity.getPrice().decimalPart());
                statement.setString(7, orderEntity.getPaymentType().toString().toLowerCase());
                statement.setTimestamp(8, Timestamp.valueOf(orderEntity.getPaymentAt()));
                statement.setTimestamp(9, Timestamp.valueOf(orderEntity.getUpdatedAt()));
                statement.setTimestamp(10, Timestamp.valueOf(orderEntity.getCreatedAt()));

                LOGGER.info("Формування запиту на збереження.");
            } else {
                statement.setString(1, orderEntity.getClientEntity().getId().toString());
                statement.setString(2, orderEntity.getCarEntity().getId().toString());
                statement.setInt(3, orderEntity.getDiscountEntity().getId());
                statement.setInt(4, orderEntity.getPrice().wholePart());
                statement.setInt(5, orderEntity.getPrice().decimalPart());
                statement.setString(6, orderEntity.getPaymentType().toString().toLowerCase());
                statement.setTimestamp(7, Timestamp.valueOf(orderEntity.getPaymentAt()));
                statement.setTimestamp(8, Timestamp.valueOf(orderEntity.getUpdatedAt()));
                statement.setTimestamp(9, Timestamp.valueOf(orderEntity.getCreatedAt()));
                statement.setString(10, orderEntity.getId().toString());

                LOGGER.info("Формування запиту на оновлення.");
            }
            statement.executeUpdate();

            LOGGER.info("Успішне оновлення або збереження об'єкту: {}", orderEntity);
            return orderEntity;
        } catch (SQLException e) {
            String message = "При збереженні або оновленні запису в %s. Детально: %s"
                    .formatted(OrderDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Delete an order from the database table by identifier.
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
                    .formatted(OrderDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public List<EmployeeEntity> findAllStaff(UUID orderId) {
        try (Connection connection = ConnectionManager.get();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL_STAFF_IN_ORDER_SQL)) {
            statement.setString(1, orderId.toString());
            ResultSet resultSet = statement.executeQuery();
            var employeeEntities = new ArrayList<EmployeeEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                employeeEntities.add(EmployeeEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", employeeEntities.size(), FIND_ALL_STAFF_IN_ORDER_SQL);
            return employeeEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів employeeEntities в %s. Детально: %s"
                    .formatted(OrderDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public void attachToEmployee(UUID orderId, UUID employeeId) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(SAVE_EMPLOYEE_IN_ORDER_SQL)) {

            statement.setString(1, orderId.toString());
            statement.setString(2, employeeId.toString());

            statement.executeUpdate();
            LOGGER.info("Успішне прикріплення об'єкту employee({}) до order({}).", orderId, employeeId);
        } catch (SQLException e) {
            String message = "При прикріпленні employee запису в %s. Детально: %s"
                    .formatted(OrderDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public void detachFromEmployee(UUID orderId, UUID employeeId) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(REMOVE_EMPLOYEE_FROM_ORDER_SQL)) {

            statement.setString(1, orderId.toString());
            statement.setString(2, employeeId.toString());

            statement.executeUpdate();
            LOGGER.info("Успішне від'єднання об'єкту employee({}) від order({}).", orderId, employeeId);
        } catch (SQLException e) {
            String message = "При від'єднанні employee запису в %s. Детально: %s"
                    .formatted(OrderDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public List<ServiceEntity> findAllServices(UUID orderId) {
        try (Connection connection = ConnectionManager.get();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL_SERVICES_IN_ORDER_SQL)) {
            statement.setString(1, orderId.toString());
            ResultSet resultSet = statement.executeQuery();
            var serviceEntities = new ArrayList<ServiceEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                serviceEntities.add(ServiceEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", serviceEntities.size(), FIND_ALL_SERVICES_IN_ORDER_SQL);
            return serviceEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів serviceEntities в %s. Детально: %s"
                    .formatted(OrderDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public String findServiceOrderDescription(UUID orderId, Integer serviceId) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_DESCRIPTION_SERVICE_ORDER)) {
            statement.setString(1, orderId.toString());
            statement.setInt(2, serviceId);
            var resultSet = statement.executeQuery();
            String description = null;
            if (resultSet.next()) {
                description = resultSet.getString("description");

                LOGGER.info("Знайдений опис: %s. Запит: %s".formatted(description, FIND_DESCRIPTION_SERVICE_ORDER));
            }
            return description;
        } catch (SQLException e) {
            String message = "При знаходженні опису service_order в %s. Детально: %s"
                    .formatted(OrderDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new EntityNotFoundException(message);
        }
    }

    @Override
    public void attachToService(UUID orderId, Integer serviceId, String description) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(SAVE_SERVICE_IN_ORDER_SQL)) {

            statement.setString(1, orderId.toString());
            statement.setInt(2, serviceId);
            statement.setString(3, description);

            statement.executeUpdate();
            LOGGER.info("Успішне прикріплення об'єкту service({}) до order({}).", orderId, serviceId);
        } catch (SQLException e) {
            String message = "При прикріпленні service запису в %s. Детально: %s"
                    .formatted(OrderDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public void detachFromService(UUID orderId, Integer serviceId) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(REMOVE_SERVICE_FROM_ORDER_SQL)) {

            statement.setString(1, orderId.toString());
            statement.setInt(2, serviceId);

            statement.executeUpdate();
            LOGGER.info("Успішне від'єднання об'єкту service({}) від order({}).", orderId, serviceId);
        } catch (SQLException e) {
            String message = "При від'єднанні service запису в %s. Детально: %s"
                    .formatted(OrderDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public List<SpareEntity> findAllSpares(UUID orderId) {
        try (Connection connection = ConnectionManager.get();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL_SPARES_IN_ORDER_SQL)) {
            statement.setString(1, orderId.toString());
            ResultSet resultSet = statement.executeQuery();
            var spareEntities = new ArrayList<SpareEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                spareEntities.add(SpareEntityRowConverter.getInstance().execute(resultSet));
            }
            return spareEntities;
        } catch (SQLException e) {
            throw new PersistenceException(
                    "При знаходженні всіх записів spareEntities в %s".formatted(OrderDaoImpl.class.getSimpleName()));
        }
    }

    @Override
    public int findSpareOrderQuantity(UUID orderId, UUID spareId) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_QUANTITY_SPARE_ORDER)) {
            statement.setString(1, orderId.toString());
            statement.setString(2, spareId.toString());
            var resultSet = statement.executeQuery();
            int quantity = -1;
            if (resultSet.next()) {
                quantity = resultSet.getInt("quantity");

                LOGGER.info("Знайдена кількість: %s. Запит: %s".formatted(quantity, FIND_QUANTITY_SPARE_ORDER));
            }
            return quantity;
        } catch (SQLException e) {
            String message = "При знаходженні опису service_order в %s. Детально: %s"
                    .formatted(OrderDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new EntityNotFoundException(message);
        }
    }

    @Override
    public void attachToSpare(UUID orderId, UUID spareId, int quantity) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(SAVE_SPARE_IN_ORDER_SQL)) {

            statement.setString(1, orderId.toString());
            statement.setString(2, spareId.toString());
            statement.setInt(3, quantity);

            statement.executeUpdate();
            LOGGER.info("Успішне прикріплення об'єкту spare({}) до order({}).", orderId, spareId);
        } catch (SQLException e) {
            String message = "При прикріпленні spare запису в %s. Детально: %s"
                    .formatted(OrderDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public void detachFromSpare(UUID orderId, UUID spareId) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(REMOVE_SPARE_FROM_ORDER_SQL)) {

            statement.setString(1, orderId.toString());
            statement.setString(2, spareId.toString());

            statement.executeUpdate();
            LOGGER.info("Успішне від'єднання об'єкту spare({}) від order({}).", orderId, spareId);
        } catch (SQLException e) {
            String message = "При від'єднанні spare запису в %s. Детально: %s"
                    .formatted(OrderDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    private OrderDaoImpl() {}

    private static class SingletonHolder {
        public static final OrderDaoImpl INSTANCE = new OrderDaoImpl();
    }

    public static OrderDaoImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
