package com.arakviel.carrepair.persistence.impl.converter.impl;

import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.entity.impl.BrandEntity;
import com.arakviel.carrepair.persistence.entity.impl.ModelEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.NoResultException;
import com.arakviel.carrepair.persistence.impl.converter.EntityRowConverter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelEntityRowConverter implements EntityRowConverter<ModelEntity> {

    public static final Logger LOGGER = LoggerFactory.getLogger(ModelEntityRowConverter.class);

    @Override
    public ModelEntity execute(ResultSet resultSet) {
        try {
            Connection connection = resultSet.getStatement().getConnection();
            BrandEntity brandEntity = DaoFactory.getInstance()
                    .getBrandDao()
                    .findOneById(resultSet.getInt("brand_id"), connection)
                    .orElseThrow(() -> new EntityNotFoundException("Не вдалось отримати об'єкт"
                                                                   + " сутності BrandEntity,"
                                                                   + " так як вона відсутня в"
                                                                   + " базі даних."));
            var modelEntity = ModelEntity.builder()
                    .id(resultSet.getInt("id"))
                    .brandEntity(brandEntity)
                    .name(resultSet.getString("name"))
                    .build();
            LOGGER.info("Результат конвертації ResultSet в об'єктний тип: {}", modelEntity);
            return modelEntity;
        } catch (SQLException e) {
            String message = "Не вдалось отримати ResultSet в %s. Детально: %s"
                    .formatted(AddressEntityRowConverter.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new NoResultException(message);
        }
    }

    private ModelEntityRowConverter() {}

    private static class SingletonHolder {
        public static final ModelEntityRowConverter INSTANCE = new ModelEntityRowConverter();
    }

    public static ModelEntityRowConverter getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
