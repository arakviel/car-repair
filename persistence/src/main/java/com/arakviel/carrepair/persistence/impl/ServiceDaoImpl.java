package com.arakviel.carrepair.persistence.impl;

import static java.util.stream.Collectors.joining;

import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.dao.ServiceDao;
import com.arakviel.carrepair.persistence.entity.impl.OrderEntity;
import com.arakviel.carrepair.persistence.entity.impl.ServiceEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.PersistenceException;
import com.arakviel.carrepair.persistence.filter.impl.ServiceFilterDto;
import com.arakviel.carrepair.persistence.impl.converter.impl.OrderEntityRowConverter;
import com.arakviel.carrepair.persistence.impl.converter.impl.ServiceEntityRowConverter;
import com.arakviel.carrepair.persistence.util.ConnectionManager;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceDaoImpl implements ServiceDao {

    public static final Logger LOGGER = LoggerFactory.getLogger(ServiceDaoImpl.class);

    private static final String FIND_ALL_SQL =
            """
            SELECT id,
                   name,
                   description,
                   photo,
                   currency_id,
                   price_whole_part,
                   price_decimal_part
              FROM services
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;
    private static final String SAVE_SQL =
            """
            INSERT INTO services(name, description, photo, currency_id, price_whole_part, price_decimal_part)
            VALUES (?, ?, ?, ?, ?, ?);
            """;
    private static final String UPDATE_SQL =
            """
            UPDATE services
               SET name = ?,
                   description = ?,
                   photo = ?,
                   currency_id = ?,
                   price_whole_part = ?,
                   price_decimal_part = ?
             WHERE id = ?;
            """;

    private static final String REMOVE_SQL =
            """
            DELETE FROM services
                  WHERE id = ?;
            """;

    private static final String FIND_ALL_ORDERS_IN_SERVICES_SQL =
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
                   JOIN service_order AS eo
                     ON o.id = eo.order_id
             WHERE eo.service_id = ?;
            """;

    /**
     * Get an ServiceEntity object by identifier.
     *
     * @param id primary key identifier
     * @return Optional<ServiceEntity>
     */
    @Override
    public Optional<ServiceEntity> findOneById(Integer id) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setInt(1, id);
            var resultSet = statement.executeQuery();
            ServiceEntity serviceEntity = null;
            if (resultSet.next()) {
                serviceEntity = ServiceEntityRowConverter.getInstance().execute(resultSet);

                LOGGER.info("Founded entity {} by id = {}. Запит: {}", serviceEntity, id, FIND_BY_ID_SQL);
            }
            return Optional.ofNullable(serviceEntity);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(ServiceDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<ServiceEntity> findAll() {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = statement.executeQuery();
            var serviceEntities = new ArrayList<ServiceEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                serviceEntities.add(ServiceEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", serviceEntities.size(), FIND_ALL_SQL);
            return serviceEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів в %s. Детально: %s"
                    .formatted(ServiceDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<ServiceEntity> findAll(ServiceFilterDto filter) {
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
        if (Objects.nonNull(filter.price())) {
            whereSQL.add("price_whole_part = ?");
            parameters.add(filter.price().wholePart());
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
            var serviceEntities = new ArrayList<ServiceEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                serviceEntities.add(ServiceEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей після фільтрації: {}. Запит: {}", serviceEntities.size(), sql);
            return serviceEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів за фільтром %s в %s. Детально: %s"
                    .formatted(where, ServiceDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Save or update the serviceEntity to the database table.
     *
     * @param serviceEntity persistent serviceEntity
     * @return serviceEntity, with identifier of the last added record from the database
     */
    @Override
    public ServiceEntity save(ServiceEntity serviceEntity) {
        boolean isNullId = Objects.isNull(serviceEntity.getId());
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(
                        isNullId ? SAVE_SQL : UPDATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, serviceEntity.getName());
            statement.setString(2, serviceEntity.getDescription());
            statement.setBlob(3, new ByteArrayInputStream(serviceEntity.getPhoto()));
            statement.setInt(4, serviceEntity.getCurrencyEntity().getId());
            statement.setInt(5, serviceEntity.getPrice().wholePart());
            statement.setInt(6, serviceEntity.getPrice().decimalPart());

            if (isNullId) {
                LOGGER.info("Формування запиту на збереження.");
            } else {
                statement.setInt(7, serviceEntity.getId());

                LOGGER.info("Формування запиту на оновлення.");
            }
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                serviceEntity.setId(generatedKeys.getInt(1));
            }

            LOGGER.info("Успішне оновлення або збереження об'єкту: {}", serviceEntity);
            return serviceEntity;
        } catch (SQLException e) {
            String message = "При збереженні або оновленні запису в %s. Детально: %s"
                    .formatted(ServiceDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Delete a service from the database table by identifier.
     *
     * @param id primary key identifier
     */
    @Override
    public void remove(Integer id) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(REMOVE_SQL)) {
            statement.setString(1, id.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            String message = "Помилка при операції видалення рядка таблиці в %s. Детально: %s"
                    .formatted(ServiceDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public List<OrderEntity> findAllOrders(Integer serviceId) {
        try (Connection connection = ConnectionManager.get();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL_ORDERS_IN_SERVICES_SQL)) {
            statement.setInt(1, serviceId);
            ResultSet resultSet = statement.executeQuery();
            var orderEntities = new ArrayList<OrderEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                orderEntities.add(OrderEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", orderEntities.size(), FIND_ALL_ORDERS_IN_SERVICES_SQL);
            return orderEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів order в %s. Детально: %s"
                    .formatted(EmployeeDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public void attachToOrder(Integer serviceId, UUID orderId, String description) {
        DaoFactory.getInstance().getOrderDao().attachToService(orderId, serviceId, description);
    }

    @Override
    public void detachFromOrder(Integer serviceId, UUID orderId) {
        DaoFactory.getInstance().getOrderDao().detachFromService(orderId, serviceId);
    }

    public ServiceDaoImpl() {}

    private static class SingletonHolder {
        public static final ServiceDaoImpl INSTANCE = new ServiceDaoImpl();
    }

    public static ServiceDaoImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
