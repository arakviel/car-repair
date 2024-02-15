package com.arakviel.carrepair.persistence.impl.converter.impl;

import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.entity.impl.EmployeeEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.NoResultException;
import com.arakviel.carrepair.persistence.impl.converter.EntityRowConverter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmployeeEntityRowConverter implements EntityRowConverter<EmployeeEntity> {

    public static final Logger LOGGER = LoggerFactory.getLogger(EmployeeEntityRowConverter.class);

    @Override
    public EmployeeEntity execute(ResultSet resultSet) {
        try {
            Connection connection = resultSet.getStatement().getConnection();

            var addressEntity = DaoFactory.getInstance()
                    .getAddressDao()
                    .findOneById(UUID.fromString(resultSet.getString("address_id")), connection)
                    .orElseThrow(() -> new EntityNotFoundException("Не вдалось отримати об'єкт сутності"
                                                                   + " AddressEntity, так як вона відсутня в"
                                                                   + " базі даних."));

            var workroomEntity = DaoFactory.getInstance()
                    .getWorkroomDao()
                    .findOneById(UUID.fromString(resultSet.getString("workroom_id")), connection)
                    .orElseThrow(() -> new EntityNotFoundException("Не вдалось отримати об'єкт сутності"
                            + " WorkroomEntity, так як вона відсутня в"
                            + " базі даних."));

            var positionEntity = DaoFactory.getInstance()
                    .getPositionDao()
                    .findOneById(resultSet.getInt("position_id"), connection)
                    .orElseThrow(() -> new EntityNotFoundException("Не вдалось отримати об'єкт сутності"
                            + " PositionEntity, так як вона відсутня в"
                            + " базі даних."));

            var spareEntity = EmployeeEntity.builder()
                    .id(UUID.fromString(resultSet.getString("id")))
                    .addressEntity(addressEntity)
                    .workroomEntity(workroomEntity)
                    .positionEntity(positionEntity)
                    .firstName(resultSet.getString("first_name"))
                    .lastName(resultSet.getString("last_name"))
                    .photo(resultSet.getBytes("photo"))
                    .passportDocCopy(resultSet.getBytes("passport_doc_copy"))
                    .build();

            spareEntity.setMiddleName(resultSet.getObject("middle_name", String.class));
            spareEntity.setBankNumberDocCopy(resultSet.getBytes("bank_number_doc_copy"));
            spareEntity.setOtherDocCopy(resultSet.getBytes("other_doc_copy"));
            spareEntity.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
            spareEntity.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
            LOGGER.info("Результат конвертації ResultSet в об'єктний тип: {}", spareEntity);
            return spareEntity;
        } catch (SQLException e) {
            String message = "Не вдалось отримати ResultSet в %s. Детально: %s"
                    .formatted(EmployeeEntityRowConverter.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new NoResultException(message);
        }
    }

    private EmployeeEntityRowConverter() {}

    private static class SingletonHolder {
        public static final EmployeeEntityRowConverter INSTANCE = new EmployeeEntityRowConverter();
    }

    public static EmployeeEntityRowConverter getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
