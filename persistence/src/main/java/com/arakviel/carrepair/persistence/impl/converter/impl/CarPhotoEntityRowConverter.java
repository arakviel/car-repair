package com.arakviel.carrepair.persistence.impl.converter.impl;

import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.entity.impl.CarPhotoEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.NoResultException;
import com.arakviel.carrepair.persistence.impl.converter.EntityRowConverter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarPhotoEntityRowConverter implements EntityRowConverter<CarPhotoEntity> {

    public static final Logger LOGGER = LoggerFactory.getLogger(CarPhotoEntityRowConverter.class);

    @Override
    public CarPhotoEntity execute(ResultSet resultSet) {
        try {
            var carEntity = DaoFactory.getInstance()
                    .getCarDao()
                    .findOneById(UUID.fromString(resultSet.getString("car_id")))
                    .orElseThrow(() -> new EntityNotFoundException("Не вдалось отримати об'єкт сутності"
                                                                   + " CarEntity, так як вона відсутня в"
                                                                   + " базі даних."));
            var phoneEntity = CarPhotoEntity.builder()
                    .id(UUID.fromString(resultSet.getString("id")))
                    .carEntity(carEntity)
                    .photo(resultSet.getBytes("photo"))
                    .build();
            phoneEntity.setDescription(resultSet.getObject("description", String.class));

            LOGGER.info("Результат конвертації ResultSet в об'єктний тип: {}", phoneEntity);
            return phoneEntity;
        } catch (SQLException e) {
            String message = "Не вдалось отримати ResultSet в %s. Детально: %s"
                    .formatted(CarPhotoEntityRowConverter.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new NoResultException(message);
        }
    }

    private CarPhotoEntityRowConverter() {}

    private static class SingletonHolder {
        public static final CarPhotoEntityRowConverter INSTANCE = new CarPhotoEntityRowConverter();
    }

    public static CarPhotoEntityRowConverter getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
