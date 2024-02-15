package com.arakviel.carrepair.persistence.impl.converter.impl;

import com.arakviel.carrepair.persistence.entity.impl.BrandEntity;
import com.arakviel.carrepair.persistence.exception.persistance.NoResultException;
import com.arakviel.carrepair.persistence.impl.converter.EntityRowConverter;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrandEntityRowConverter implements EntityRowConverter<BrandEntity> {

    public static final Logger LOGGER = LoggerFactory.getLogger(BrandEntityRowConverter.class);

    @Override
    public BrandEntity execute(ResultSet resultSet) {
        try {
            var brandEntity = BrandEntity.builder()
                    .id(resultSet.getInt("id"))
                    .name(resultSet.getString("name"))
                    .build();
            LOGGER.info("Результат конвертації ResultSet в об'єктний тип: {}", brandEntity);
            return brandEntity;
        } catch (SQLException e) {
            String message = "Не вдалось отримати ResultSet в %s. Детально: %s"
                    .formatted(AddressEntityRowConverter.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new NoResultException(message);
        }
    }

    private BrandEntityRowConverter() {}

    private static class SingletonHolder {
        public static final BrandEntityRowConverter INSTANCE = new BrandEntityRowConverter();
    }

    public static BrandEntityRowConverter getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
