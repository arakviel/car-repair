package com.arakviel.carrepair.persistence.impl;

import static java.util.stream.Collectors.joining;

import com.arakviel.carrepair.persistence.dao.PayrollDao;
import com.arakviel.carrepair.persistence.entity.impl.PayrollEntity;
import com.arakviel.carrepair.persistence.entity.impl.WorkroomEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.PersistenceException;
import com.arakviel.carrepair.persistence.filter.impl.PayrollFilterDto;
import com.arakviel.carrepair.persistence.impl.converter.impl.PayrollEntityRowConverter;
import com.arakviel.carrepair.persistence.util.ConnectionManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PayrollDaoImpl implements PayrollDao {

    public static final Logger LOGGER = LoggerFactory.getLogger(PayrollDaoImpl.class);

    private static final String FIND_ALL_SQL =
            """
            SELECT id,
                   employee_id,
                   period_type,
                   hour_count,
                   salary_whole_part,
                   salary_decimal_part,
                   payment_at,
                   updated_at,
                   created_at
              FROM payrolls
            """;
    private static final String FIND_ALL_BY_WORKROOM_SQL =
            """
            SELECT pr.id,
                   pr.employee_id,
                   pr.period_type,
                   pr.hour_count,
                   pr.salary_whole_part,
                   pr.salary_decimal_part,
                   pr.payment_at,
                   pr.updated_at,
                   pr.created_at
              FROM payrolls AS pr
                   JOIN staff AS s
                     ON pr.employee_id = s.id
             WHERE s.workroom_id = ?;
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;
    private static final String SAVE_SQL =
            """
            INSERT INTO payrolls(id, employee_id, period_type, hour_count, salary_whole_part, salary_decimal_part, payment_at, updated_at, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;
    private static final String UPDATE_SQL =
            """
            UPDATE payrolls
               SET employee_id = ?,
                   period_type = ?,
                   hour_count = ?,
                   salary_whole_part = ?,
                   salary_decimal_part = ?,
                   payment_at = ?,
                   updated_at = ?,
                   created_at = ?
             WHERE id = ?;
            """;

    private static final String REMOVE_SQL =
            """
            DELETE FROM payrolls
                  WHERE id = ?;
            """;

    /**
     * Get an PayrollEntity object by identifier.
     *
     * @param id primary key identifier
     * @return Optional<PayrollEntity>
     */
    @Override
    public Optional<PayrollEntity> findOneById(UUID id) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setString(1, id.toString());
            var resultSet = statement.executeQuery();
            PayrollEntity payrollEntity = null;
            if (resultSet.next()) {
                payrollEntity = PayrollEntityRowConverter.getInstance().execute(resultSet);

                LOGGER.info("Founded entity %s by id = %s. Запит: %s"
                        .formatted(payrollEntity, id.toString(), FIND_BY_ID_SQL));
            }
            return Optional.ofNullable(payrollEntity);
        } catch (SQLException e) {
            String message = "При знаходженні запису по id в %s. Детально: %s"
                    .formatted(PayrollDaoImpl.class.getSimpleName(), e.getMessage());
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
    public List<PayrollEntity> findAll() {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = statement.executeQuery();
            var payrollEntities = new ArrayList<PayrollEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                payrollEntities.add(PayrollEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", payrollEntities.size(), FIND_ALL_SQL);
            return payrollEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів в %s. Детально: %s"
                    .formatted(PayrollDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public List<PayrollEntity> findAllByWorkroomEntity(WorkroomEntity workroomEntity) {
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(FIND_ALL_BY_WORKROOM_SQL)) {
            statement.setString(1, workroomEntity.getId().toString());
            var resultSet = statement.executeQuery();
            var payrollEntities = new ArrayList<PayrollEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                payrollEntities.add(PayrollEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей: {}. Запит: {}", payrollEntities.size(), FIND_ALL_BY_WORKROOM_SQL);
            return payrollEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів в %s. Детально: %s"
                    .formatted(PayrollDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    @Override
    public List<PayrollEntity> findAllByWorkroomEntity(PayrollFilterDto filter, WorkroomEntity workroomEntity) {
        return findAll(filter, FIND_ALL_BY_WORKROOM_SQL, workroomEntity);
    }

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @param filter values for where conditions
     * @return collection of Entities
     */
    @Override
    public List<PayrollEntity> findAll(PayrollFilterDto filter) {
        return findAll(filter, FIND_ALL_SQL, null);
    }

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @param filter values for where conditions
     * @return collection of Entities
     */
    private List<PayrollEntity> findAll(PayrollFilterDto filter, String innerSql, WorkroomEntity workroomEntity) {
        var parameters = new ArrayList<>();
        var whereSQL = new ArrayList<String>();

        if (Objects.nonNull(workroomEntity)) {
            parameters.add(workroomEntity.getId().toString());
        }
        if (Objects.nonNull(filter.employeeEntity())) {
            whereSQL.add("employee_id = ?");
            parameters.add(filter.employeeEntity().getId().toString());
        }
        if (Objects.nonNull(filter.periodType())) {
            whereSQL.add("period_type LIKE ?");
            parameters.add("%" + filter.periodType() + "%");
        }
        if (Objects.nonNull(filter.hourCount())) {
            whereSQL.add("hour_count = ?");
            parameters.add(filter.hourCount());
        }
        if (Objects.nonNull(filter.salary())) {
            whereSQL.add("salary_whole_part = ?");
            parameters.add(filter.salary().wholePart());
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
            var payrollEntities = new ArrayList<PayrollEntity>(resultSet.getFetchSize());
            while (resultSet.next()) {
                payrollEntities.add(PayrollEntityRowConverter.getInstance().execute(resultSet));
            }

            LOGGER.info("Кількість сутностей після фільтрації: {}. Запит: {}", payrollEntities.size(), sql);
            return payrollEntities;
        } catch (SQLException e) {
            String message = "При знаходженні всіх записів за фільтром %s в %s. Детально: %s"
                    .formatted(where, PayrollDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Save or update the payroll to the database table.
     *
     * @param payrollEntity persistent payroll
     * @return payroll, with identifier of the last added record from the database
     */
    @Override
    public PayrollEntity save(PayrollEntity payrollEntity) {
        boolean isNullId = Objects.isNull(payrollEntity.getId());
        try (var connection = ConnectionManager.get();
                var statement = connection.prepareStatement(isNullId ? SAVE_SQL : UPDATE_SQL)) {
            if (isNullId) {
                var id = UUID.randomUUID();
                payrollEntity.setId(id);
                statement.setString(1, id.toString());
                statement.setString(2, payrollEntity.getEmployeeEntity().getId().toString());
                statement.setString(3, payrollEntity.getPeriodType().toString().toLowerCase());
                statement.setInt(4, payrollEntity.getHourCount());
                statement.setInt(5, payrollEntity.getSalary().wholePart());
                statement.setInt(6, payrollEntity.getSalary().decimalPart());
                statement.setTimestamp(7, Timestamp.valueOf(payrollEntity.getPaymentAt()));
                statement.setTimestamp(8, Timestamp.valueOf(payrollEntity.getUpdatedAt()));
                statement.setTimestamp(9, Timestamp.valueOf(payrollEntity.getCreatedAt()));

                LOGGER.info("Формування запиту на збереження.");
            } else {
                statement.setString(1, payrollEntity.getEmployeeEntity().getId().toString());
                statement.setString(2, payrollEntity.getPeriodType().toString().toLowerCase());
                statement.setInt(3, payrollEntity.getHourCount());
                statement.setInt(4, payrollEntity.getSalary().wholePart());
                statement.setInt(5, payrollEntity.getSalary().decimalPart());
                statement.setTimestamp(6, Timestamp.valueOf(payrollEntity.getPaymentAt()));
                statement.setTimestamp(7, Timestamp.valueOf(payrollEntity.getUpdatedAt()));
                statement.setTimestamp(8, Timestamp.valueOf(payrollEntity.getCreatedAt()));
                statement.setString(9, payrollEntity.getId().toString());

                LOGGER.info("Формування запиту на оновлення.");
            }
            statement.executeUpdate();

            LOGGER.info("Успішне оновлення або збереження об'єкту: {}", payrollEntity);
            return payrollEntity;
        } catch (SQLException e) {
            String message = "При збереженні або оновленні запису в %s. Детально: %s"
                    .formatted(PayrollDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    /**
     * Delete a payroll from the database table by identifier.
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
                    .formatted(PayrollDaoImpl.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new PersistenceException(message);
        }
    }

    private PayrollDaoImpl() {}

    private static class SingletonHolder {

        public static final PayrollDaoImpl INSTANCE = new PayrollDaoImpl();
    }

    public static PayrollDaoImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
