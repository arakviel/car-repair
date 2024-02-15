package com.arakviel.carrepair.persistence.impl;

import static java.util.stream.Collectors.joining;

import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.dao.SpareDao;
import com.arakviel.carrepair.persistence.entity.impl.OrderEntity;
import com.arakviel.carrepair.persistence.entity.impl.SpareEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.PersistenceException;
import com.arakviel.carrepair.persistence.filter.impl.SpareFilterDto;
import com.arakviel.carrepair.persistence.impl.converter.impl.OrderEntityRowConverter;
import com.arakviel.carrepair.persistence.impl.converter.impl.SpareEntityRowConverter;
import com.arakviel.carrepair.persistence.util.ConnectionManager;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpareDaoImpl implements SpareDao {

    public static final Logger LOGGER = LoggerFactory.getLogger(SpareDaoImpl.class);

    private static final String FIND_ALL_SQL =
            """
            SELECT id,
                   workroom_id,
                   name,
                   description,
                   photo,
                   price_whole_part,
                   price_decimal_part,
                   quantity_in_stock
              FROM spares
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;
    private static final String SAVE_SQL =
            """
            INSERT INTO spares(id, workroom_id, name, description, photo, price_whole_part, price_decimal_part, quantity_in_stock)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?);
            """;
    private static final String UPDATE_SQL =
            """
            UPDATE spares
               SET workroom_id = ?,
                   name = ?,
                   description = ?,
                   photo = ?,
                   price_whole_part = ?,
                   price_decimal_part = ?,
                   quantity_in_stock = ?
             WHERE id = ?;
            """;

    private static final String REMOVE_SQL =
            """
            DELETE FROM spares
                  WHERE id = ?;
            """;

    private static final String FIND_ALL_ORDERS_IN_SPARES_SQL =
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
                   JOIN spare_order AS eo
                     ON o.id = eo.order_id
             WHERE eo.spare_id = ?;
            """;

    /**
     * Get an SpareEntity object by identifier.
     *
     * @param id primary key identifier
     * @return Optional<SpareEntity>
     */
    @Override
    public Optional<SpareEntity> findOneById(UUID id) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setString(1, id.toString());
            var resultSet = statement.executeQuery();
            SpareEntity spareEntity = null;
            if (resultSet.next()) {
                spareEntity = SpareEntityRowConverter.getInstance().execute(resultSet);

                LOGGER.info("Founded entity %s by id = %s. Запит: %s"
                        .formatted(spareEntity, id.toString(), FIND_BY_ID_SQL));
            }
            return Optional.ofNullable(spareEntity);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(SpareDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<SpareEntity> findAll() {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = statement.executeQuery();
            var spareEntities = new ArrayList<SpareEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                spareEntities.add(SpareEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", spareEntities.size(), FIND_ALL_SQL);
            return spareEntities;
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
    public List<SpareEntity> findAll(SpareFilterDto filter) {
        var parameters = new ArrayList<>();
        var whereSQL = new ArrayList<String>();

        if (Objects.nonNull(filter.workroomEntity())) {
            whereSQL.add("workroom_id = ?");
            parameters.add(filter.workroomEntity().getId());
        }
        if (Objects.nonNull(filter.name())) {
            whereSQL.add("name LIKE ?");
            parameters.add("%" + filter.name() + "%");
        }
        if (Objects.nonNull(filter.price())) {
            whereSQL.add("price_whole_part = ?");
            parameters.add(filter.price().wholePart());
        }
        if (Objects.nonNull(filter.quantityInStock())) {
            whereSQL.add("quantity_in_stock = ?");
            parameters.add(filter.quantityInStock());
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
            var spareEntities = new ArrayList<SpareEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                spareEntities.add(SpareEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей після фільтрації: {}. Запит: {}", spareEntities.size(), sql);
            return spareEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів за фільтром %s в %s. Детально: %s"
                    .formatted(where, SpareDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Save or update the spare to the database table.
     *
     * @param spareEntity persistent spare
     * @return spare, with identifier of the last added record from the database
     */
    @Override
    public SpareEntity save(SpareEntity spareEntity) {
        boolean isNullId = Objects.isNull(spareEntity.getId());
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(isNullId ? SAVE_SQL : UPDATE_SQL)) {
            byte[] photo = Objects.nonNull(spareEntity.getPhoto()) ? spareEntity.getPhoto() : null;

            if (isNullId) {
                var id = UUID.randomUUID();
                spareEntity.setId(id);
                statement.setString(1, id.toString());
                statement.setString(2, spareEntity.getWorkroomEntity().getId().toString());
                statement.setString(3, spareEntity.getName());
                statement.setString(4, spareEntity.getDescription());
                if (Objects.nonNull(spareEntity.getPhoto())) {
                    statement.setBlob(5, new ByteArrayInputStream(spareEntity.getPhoto()));
                } else {
                    statement.setBytes(5, null);
                }
                statement.setInt(6, spareEntity.getPrice().wholePart());
                statement.setInt(7, spareEntity.getPrice().decimalPart());
                statement.setInt(8, spareEntity.getQuantityInStock());

                LOGGER.info("Формування запиту на збереження.");
            } else {
                statement.setString(1, spareEntity.getWorkroomEntity().getId().toString());
                statement.setString(2, spareEntity.getName());
                statement.setString(3, spareEntity.getDescription());
                if (Objects.nonNull(spareEntity.getPhoto())) {
                    statement.setBlob(4, new ByteArrayInputStream(spareEntity.getPhoto()));
                } else {
                    statement.setBytes(4, null);
                }
                statement.setInt(5, spareEntity.getPrice().wholePart());
                statement.setInt(6, spareEntity.getPrice().decimalPart());
                statement.setInt(7, spareEntity.getQuantityInStock());
                statement.setString(8, spareEntity.getId().toString());

                LOGGER.info("Формування запиту на оновлення.");
            }
            statement.executeUpdate();

            LOGGER.info("Успішне оновлення або збереження об'єкту: {}", spareEntity);
            return spareEntity;
        } catch (SQLException e) {
            String message = "При збереженні або оновленні запису в %s. Детально: %s"
                    .formatted(SpareDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Delete a spare from the database table by identifier.
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
                    .formatted(SpareDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public List<OrderEntity> findAllOrders(UUID spareId) {
        try (Connection connection = ConnectionManager.get();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL_ORDERS_IN_SPARES_SQL)) {
            statement.setString(1, spareId.toString());
            ResultSet resultSet = statement.executeQuery();
            var orderEntities = new ArrayList<OrderEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                orderEntities.add(OrderEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", orderEntities.size(), FIND_ALL_ORDERS_IN_SPARES_SQL);
            return orderEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів order в %s. Детально: %s"
                    .formatted(EmployeeDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public void attachToOrder(UUID spareId, UUID orderId, int quantity) {
        DaoFactory.getInstance().getOrderDao().attachToSpare(orderId, spareId, quantity);
    }

    @Override
    public void detachFromOrder(UUID spareId, UUID orderId) {
        DaoFactory.getInstance().getOrderDao().detachFromSpare(orderId, spareId);
    }

    private SpareDaoImpl() {}

    private static class SingletonHolder {
        public static final SpareDaoImpl INSTANCE = new SpareDaoImpl();
    }

    public static SpareDaoImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
