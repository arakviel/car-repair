package com.arakviel.carrepair.persistence.impl.converter.impl;

import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.entity.impl.EmployeeEntity;
import com.arakviel.carrepair.persistence.entity.impl.PhoneEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.NoResultException;
import com.arakviel.carrepair.persistence.impl.converter.EntityRowConverter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhoneEntityRowConverter implements EntityRowConverter<PhoneEntity> {

    public static final Logger LOGGER = LoggerFactory.getLogger(PhoneEntityRowConverter.class);

    @Override
    public PhoneEntity execute(ResultSet resultSet) {
        try {
            Connection connection = resultSet.getStatement().getConnection();

            EmployeeEntity employeeEntity = DaoFactory.getInstance()
                    .getEmployeeDao()
                    .findOneById(UUID.fromString(resultSet.getString("address_id")), connection)
                    .orElseThrow(() -> new EntityNotFoundException("Не вдалось отримати об'єкт сутності"
                                                                   + " EmployeeEntity, так як вона відсутня в"
                                                                   + " базі даних."));
            var phoneEntity = PhoneEntity.builder()
                    .id(UUID.fromString(resultSet.getString("id")))
                    .employeeEntity(employeeEntity)
                    .phoneType(PhoneEntity.PhoneType.valueOf(
                            resultSet.getString("type").toUpperCase()))
                    .value(resultSet.getString("value"))
                    .build();

            LOGGER.info("Результат конвертації ResultSet в об'єктний тип: {}", phoneEntity);
            return phoneEntity;
        } catch (SQLException e) {
            String message = "Не вдалось отримати ResultSet в %s. Детально: %s"
                    .formatted(PhoneEntityRowConverter.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new NoResultException(message);
        }
    }

    private PhoneEntityRowConverter() {}

    private static class SingletonHolder {
        public static final PhoneEntityRowConverter INSTANCE = new PhoneEntityRowConverter();
    }

    public static PhoneEntityRowConverter getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
