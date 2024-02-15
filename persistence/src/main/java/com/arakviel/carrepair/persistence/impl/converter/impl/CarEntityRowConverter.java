package com.arakviel.carrepair.persistence.impl.converter.impl;

import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.entity.impl.CarEntity;
import com.arakviel.carrepair.persistence.entity.impl.ModelEntity;
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

public class CarEntityRowConverter implements EntityRowConverter<CarEntity> {

    public static final Logger LOGGER = LoggerFactory.getLogger(CarEntityRowConverter.class);

    @Override
    public CarEntity execute(ResultSet resultSet) {
        try {
            Connection connection = resultSet.getStatement().getConnection();

            ModelEntity modelEntity = DaoFactory.getInstance()
                    .getModelDao()
                    .findOneById(resultSet.getInt("model_id"), connection)
                    .orElseThrow(() -> new EntityNotFoundException("Не вдалось отримати об'єкт сутності"
                                                                   + " ModelEntity, так як вона відсутня в"
                                                                   + " базі даних."));
            var carEntity = CarEntity.builder()
                    .id(UUID.fromString(resultSet.getString("id")))
                    .modelEntity(modelEntity)
                    .number(resultSet.getString("number"))
                    .year(resultSet.getShort("year"))
                    .engineType(CarEntity.EngineType.valueOf(
                            resultSet.getString("engine_type").toUpperCase()))
                    .mileage(resultSet.getInt("mileage"))
                    .build();
            carEntity.setColor(resultSet.getObject("color", String.class));
            carEntity.setUpdatedAt(resultSet.getObject("updated_at", LocalDateTime.class));
            carEntity.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));
            LOGGER.info("Результат конвертації ResultSet в об'єктний тип: {}", carEntity);
            return carEntity;
        } catch (SQLException e) {
            String message = "Не вдалось отримати ResultSet в %s. Детально: %s"
                    .formatted(AddressEntityRowConverter.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new NoResultException(message);
        }
    }

    private CarEntityRowConverter() {}

    private static class SingletonHolder {
        public static final CarEntityRowConverter INSTANCE = new CarEntityRowConverter();
    }

    public static CarEntityRowConverter getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
