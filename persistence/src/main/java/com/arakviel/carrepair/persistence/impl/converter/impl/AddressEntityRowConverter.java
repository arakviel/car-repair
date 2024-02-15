package com.arakviel.carrepair.persistence.impl.converter.impl;

import com.arakviel.carrepair.persistence.entity.impl.AddressEntity;
import com.arakviel.carrepair.persistence.exception.persistance.NoResultException;
import com.arakviel.carrepair.persistence.impl.converter.EntityRowConverter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddressEntityRowConverter implements EntityRowConverter<AddressEntity> {

    public static final Logger LOGGER = LoggerFactory.getLogger(AddressEntityRowConverter.class);

    @Override
    public AddressEntity execute(ResultSet resultSet) {
        try {
            var addressEntity = AddressEntity.builder()
                    .id(UUID.fromString(resultSet.getString("id")))
                    .country(resultSet.getString("country"))
                    .region(resultSet.getString("region"))
                    .city(resultSet.getString("city"))
                    .street(resultSet.getString("street"))
                    .home(resultSet.getString("home"))
                    .build();
            LOGGER.info("Результат конвертації ResultSet в об'єктний тип: {}", addressEntity);
            return addressEntity;
        } catch (SQLException e) {
            String message = "Не вдалось отримати ResultSet в %s. Детально: %s"
                    .formatted(AddressEntityRowConverter.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new NoResultException(message);
        }
    }

    private AddressEntityRowConverter() {}

    private static class SingletonHolder {
        public static final AddressEntityRowConverter INSTANCE = new AddressEntityRowConverter();
    }

    public static AddressEntityRowConverter getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
