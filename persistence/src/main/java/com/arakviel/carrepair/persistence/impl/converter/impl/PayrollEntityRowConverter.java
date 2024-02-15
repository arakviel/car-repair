package com.arakviel.carrepair.persistence.impl.converter.impl;

import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.entity.impl.EmployeeEntity;
import com.arakviel.carrepair.persistence.entity.impl.Money;
import com.arakviel.carrepair.persistence.entity.impl.PayrollEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.NoResultException;
import com.arakviel.carrepair.persistence.impl.converter.EntityRowConverter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PayrollEntityRowConverter implements EntityRowConverter<PayrollEntity> {

    public static final Logger LOGGER = LoggerFactory.getLogger(PayrollEntityRowConverter.class);

    @Override
    public PayrollEntity execute(ResultSet resultSet) {
        try {
            Connection connection = resultSet.getStatement().getConnection();

            EmployeeEntity employeeEntity = DaoFactory.getInstance()
                    .getEmployeeDao()
                    .findOneById(UUID.fromString(resultSet.getString("employee_id")), connection)
                    .orElseThrow(() -> new EntityNotFoundException("Не вдалось отримати об'єкт сутності"
                                                                   + " EmployeeEntity, так як вона відсутня в"
                                                                   + " базі даних."));
            var payrollEntity = PayrollEntity.builder()
                    .id(UUID.fromString(resultSet.getString("id")))
                    .employeeEntity(employeeEntity)
                    .periodType(PayrollEntity.PeriodType.valueOf(
                            resultSet.getString("period_type").toUpperCase()))
                    .hourCount(resultSet.getInt("hour_count"))
                    .salary(new Money(resultSet.getInt("salary_whole_part"), resultSet.getInt("salary_decimal_part")))
                    .paymentAt(resultSet.getTimestamp("payment_at").toLocalDateTime())
                    .build();
            payrollEntity.setUpdatedAt(resultSet.getObject("updated_at", LocalDateTime.class));
            payrollEntity.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));
            LOGGER.info("Результат конвертації ResultSet в об'єктний тип: {}", payrollEntity);
            return payrollEntity;
        } catch (SQLException e) {
            String message = "Не вдалось отримати ResultSet в %s. Детально: %s"
                    .formatted(PayrollEntityRowConverter.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new NoResultException(message);
        }
    }

    private PayrollEntityRowConverter() {}

    private static class SingletonHolder {

        public static final PayrollEntityRowConverter INSTANCE = new PayrollEntityRowConverter();
    }

    public static PayrollEntityRowConverter getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
