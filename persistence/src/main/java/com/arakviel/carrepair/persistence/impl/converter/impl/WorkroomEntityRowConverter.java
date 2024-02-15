package com.arakviel.carrepair.persistence.impl.converter.impl;

import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.entity.impl.AddressEntity;
import com.arakviel.carrepair.persistence.entity.impl.WorkroomEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.NoResultException;
import com.arakviel.carrepair.persistence.impl.converter.EntityRowConverter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkroomEntityRowConverter implements EntityRowConverter<WorkroomEntity> {

    public static final Logger LOGGER = LoggerFactory.getLogger(WorkroomEntityRowConverter.class);

    @Override
    public WorkroomEntity execute(ResultSet resultSet) {
        try {
            Connection connection = resultSet.getStatement().getConnection();

            AddressEntity addressEntity = DaoFactory.getInstance()
                    .getAddressDao()
                    .findOneById(UUID.fromString(resultSet.getString("address_id")), connection)
                    .orElseThrow(() -> new EntityNotFoundException("Не вдалось отримати об'єкт сутності"
                                                                   + " AddressEntity, так як вона відсутня в"
                                                                   + " базі даних."));
            var workroomEntity = WorkroomEntity.builder()
                    .id(UUID.fromString(resultSet.getString("id")))
                    .addressEntity(addressEntity)
                    .name(resultSet.getString("name"))
                    .photo(resultSet.getBytes("photo"))
                    .build();
            workroomEntity.setDescription(resultSet.getObject("description", String.class));
            LOGGER.info("Результат конвертації ResultSet в об'єктний тип: {}", workroomEntity);
            return workroomEntity;
        } catch (SQLException e) {
            String message = "Не вдалось отримати ResultSet в %s. Детально: %s"
                    .formatted(WorkroomEntityRowConverter.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new NoResultException(message);
        }
    }

    private WorkroomEntityRowConverter() {}

    private static class SingletonHolder {
        public static final WorkroomEntityRowConverter INSTANCE = new WorkroomEntityRowConverter();
    }

    public static WorkroomEntityRowConverter getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
