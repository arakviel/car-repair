package com.arakviel.carrepair.persistence.impl.converter.impl;

import com.arakviel.carrepair.persistence.entity.impl.DiscountEntity;
import com.arakviel.carrepair.persistence.exception.persistance.NoResultException;
import com.arakviel.carrepair.persistence.impl.converter.EntityRowConverter;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscountEntityRowConverter implements EntityRowConverter<DiscountEntity> {

    public static final Logger LOGGER = LoggerFactory.getLogger(DiscountEntityRowConverter.class);

    @Override
    public DiscountEntity execute(ResultSet resultSet) {
        try {
            var discountEntity = DiscountEntity.builder()
                    .id(resultSet.getInt("id"))
                    .name(resultSet.getString("name"))
                    .value(resultSet.getShort("value"))
                    .build();
            discountEntity.setDescription(resultSet.getObject("description", String.class));

            LOGGER.info("Результат конвертації ResultSet в об'єктний тип: {}", discountEntity);
            return discountEntity;
        } catch (SQLException e) {
            String message = "Не вдалось отримати ResultSet в %s. Детально: %s"
                    .formatted(DiscountEntityRowConverter.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new NoResultException(message);
        }
    }

    private DiscountEntityRowConverter() {}

    private static class SingletonHolder {
        public static final DiscountEntityRowConverter INSTANCE = new DiscountEntityRowConverter();
    }

    public static DiscountEntityRowConverter getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
