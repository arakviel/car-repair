package com.arakviel.carrepair.persistence.impl.converter.impl;

import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.entity.impl.Money;
import com.arakviel.carrepair.persistence.entity.impl.SpareEntity;
import com.arakviel.carrepair.persistence.entity.impl.WorkroomEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.NoResultException;
import com.arakviel.carrepair.persistence.impl.converter.EntityRowConverter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpareEntityRowConverter implements EntityRowConverter<SpareEntity> {

    public static final Logger LOGGER = LoggerFactory.getLogger(SpareEntityRowConverter.class);

    @Override
    public SpareEntity execute(ResultSet resultSet) {
        try {
            Connection connection = resultSet.getStatement().getConnection();

            WorkroomEntity workroomEntity = DaoFactory.getInstance()
                    .getWorkroomDao()
                    .findOneById(UUID.fromString(resultSet.getString("workroom_id")), connection)
                    .orElseThrow(() -> new EntityNotFoundException("Не вдалось отримати об'єкт сутності"
                                                                   + " WorkroomEntity, так як вона відсутня в"
                                                                   + " базі даних."));
            var spareEntity = SpareEntity.builder()
                    .id(UUID.fromString(resultSet.getString("id")))
                    .workroomEntity(workroomEntity)
                    .name(resultSet.getString("name"))
                    .price(new Money(resultSet.getInt("price_whole_part"), resultSet.getInt("price_decimal_part")))
                    .quantityInStock(resultSet.getInt("quantity_in_stock"))
                    .build();
            spareEntity.setDescription(resultSet.getObject("description", String.class));
            spareEntity.setPhoto(resultSet.getBytes("photo"));
            setExtraFieldOnManyToMany(resultSet, spareEntity);
            LOGGER.info("Результат конвертації ResultSet в об'єктний тип: {}", spareEntity);
            return spareEntity;
        } catch (SQLException e) {
            String message = "Не вдалось отримати ResultSet в %s. Детально: %s"
                    .formatted(SpareEntityRowConverter.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new NoResultException(message);
        }
    }

    // Не найкраща практика?
    private static void setExtraFieldOnManyToMany(ResultSet resultSet, SpareEntity spareEntity) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            if (metaData.getColumnLabel(i).equals("extra_quantity")) {
                spareEntity.setExtraFieldQuantity(resultSet.getInt("extra_quantity"));
                break;
            }
        }
    }

    private SpareEntityRowConverter() {}

    private static class SingletonHolder {

        public static final SpareEntityRowConverter INSTANCE = new SpareEntityRowConverter();
    }

    public static SpareEntityRowConverter getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
