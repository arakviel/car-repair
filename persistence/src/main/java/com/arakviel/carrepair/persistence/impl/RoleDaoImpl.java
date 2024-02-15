package com.arakviel.carrepair.persistence.impl;

import static java.util.stream.Collectors.joining;

import com.arakviel.carrepair.persistence.dao.RoleDao;
import com.arakviel.carrepair.persistence.entity.impl.RoleEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.PersistenceException;
import com.arakviel.carrepair.persistence.filter.impl.RoleFilterDto;
import com.arakviel.carrepair.persistence.impl.converter.impl.RoleEntityRowConverter;
import com.arakviel.carrepair.persistence.util.ConnectionManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoleDaoImpl implements RoleDao {

    public static final Logger LOGGER = LoggerFactory.getLogger(RoleDaoImpl.class);

    private static final String FIND_ALL_SQL =
            """
            SELECT position_id,
                   can_edit_users,
                   can_edit_spares,
                   can_edit_clients,
                   can_edit_services,
                   can_edit_orders,
                   can_edit_payrolls
              FROM roles
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE position_id = ?
            """;

    private static final String SAVE_SQL =
            """
            INSERT INTO roles(position_id, can_edit_users, can_edit_spares, can_edit_clients, can_edit_services, can_edit_orders, can_edit_payrolls)
            VALUES (?, ?, ?, ?, ?, ?, ?);
            """;

    private static final String UPDATE_SQL =
            """
            UPDATE roles
               SET can_edit_users = ?,
                   can_edit_spares = ?,
                   can_edit_clients = ?,
                   can_edit_services = ?,
                   can_edit_orders = ?,
                   can_edit_payrolls = ?
             WHERE position_id = ?;
            """;

    private static final String REMOVE_SQL =
            """
            DELETE FROM roles
                  WHERE position_id = ?;
            """;

    /**
     * Get an RoleEntity object by identifier.
     *
     * @param id primary key identifier
     * @return Optional<RoleEntity>
     */
    @Override
    public Optional<RoleEntity> findOneById(Integer id) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setInt(1, id);
            var resultSet = statement.executeQuery();
            RoleEntity roleEntity = null;
            if (resultSet.next()) {
                roleEntity = RoleEntityRowConverter.getInstance().execute(resultSet);

                LOGGER.info("Founded entity {} by id = {}. Запит: {}", roleEntity, id, FIND_BY_ID_SQL);
            }
            return Optional.ofNullable(roleEntity);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(RoleDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<RoleEntity> findAll() {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = statement.executeQuery();
            var roleEntities = new ArrayList<RoleEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                roleEntities.add(RoleEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", roleEntities.size(), FIND_ALL_SQL);
            return roleEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів в %s. Детально: %s"
                    .formatted(RoleDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<RoleEntity> findAll(RoleFilterDto filter) {
        var parameters = new ArrayList<>();
        var whereSQL = new ArrayList<String>();

        if (Objects.nonNull(filter.canEditUsers())) {
            whereSQL.add("can_edit_users = ?");
            parameters.add(filter.canEditUsers());
        }
        if (Objects.nonNull(filter.canEditSpares())) {
            whereSQL.add("can_edit_spares = ?");
            parameters.add(filter.canEditSpares());
        }
        if (Objects.nonNull(filter.canEditClients())) {
            whereSQL.add("can_edit_clients = ?");
            parameters.add(filter.canEditClients());
        }
        if (Objects.nonNull(filter.canEditServices())) {
            whereSQL.add("can_edit_services = ?");
            parameters.add(filter.canEditServices());
        }
        if (Objects.nonNull(filter.canEditOrders())) {
            whereSQL.add("can_edit_orders = ?");
            parameters.add(filter.canEditOrders());
        }
        if (Objects.nonNull(filter.canEditPayrolls())) {
            whereSQL.add("can_edit_payrolls = ?");
            parameters.add(filter.canEditPayrolls());
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
            var roleEntities = new ArrayList<RoleEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                roleEntities.add(RoleEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей після фільтрації: {}. Запит: {}", roleEntities.size(), sql);
            return roleEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів за фільтром %s в %s. Детально: %s"
                    .formatted(where, RoleDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Save or update the roleEntity to the database table.
     *
     * @param roleEntity persistent roleEntity
     * @return roleEntity, with identifier of the last added record from the database
     */
    @Override
    public RoleEntity save(RoleEntity roleEntity) {
        boolean isNullId = Objects.isNull(roleEntity.getId());
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(isNullId ? SAVE_SQL : UPDATE_SQL)) {

            if (isNullId) {
                Integer positionId = roleEntity.getPositionEntity().getId();
                roleEntity.setId(positionId);
                statement.setInt(1, positionId);
                statement.setBoolean(2, roleEntity.canEditUsers());
                statement.setBoolean(3, roleEntity.canEditSpares());
                statement.setBoolean(4, roleEntity.canEditClients());
                statement.setBoolean(5, roleEntity.canEditServices());
                statement.setBoolean(6, roleEntity.canEditOrders());
                statement.setBoolean(7, roleEntity.canEditPayrolls());

                LOGGER.info("Формування запиту на збереження.");
            } else {
                statement.setBoolean(1, roleEntity.canEditUsers());
                statement.setBoolean(2, roleEntity.canEditSpares());
                statement.setBoolean(3, roleEntity.canEditClients());
                statement.setBoolean(4, roleEntity.canEditServices());
                statement.setBoolean(5, roleEntity.canEditOrders());
                statement.setBoolean(6, roleEntity.canEditPayrolls());
                statement.setInt(7, roleEntity.getId());

                LOGGER.info("Формування запиту на оновлення.");
            }
            statement.executeUpdate();

            // Скидання positionEntity, якщо він не порожній
            if (Objects.nonNull(roleEntity.getPositionEntity())) {
                roleEntity.setPositionEntity(null);
            }

            LOGGER.info("Успішне оновлення або збереження об'єкту: {}", roleEntity);
            return roleEntity;
        } catch (SQLException e) {
            String message = "При збереженні або оновленні запису в %s. Детально: %s"
                    .formatted(RoleDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Delete a role from the database table by identifier.
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
                    .formatted(RoleDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    private RoleDaoImpl() {}

    private static class SingletonHolder {
        public static final RoleDaoImpl INSTANCE = new RoleDaoImpl();
    }

    public static RoleDaoImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
