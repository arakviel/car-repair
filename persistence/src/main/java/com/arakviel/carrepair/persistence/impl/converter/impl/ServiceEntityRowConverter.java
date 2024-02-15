package com.arakviel.carrepair.persistence.impl.converter.impl;

import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.entity.impl.Money;
import com.arakviel.carrepair.persistence.entity.impl.ServiceEntity;
import com.arakviel.carrepair.persistence.exception.persistance.EntityNotFoundException;
import com.arakviel.carrepair.persistence.exception.persistance.NoResultException;
import com.arakviel.carrepair.persistence.impl.converter.EntityRowConverter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceEntityRowConverter implements EntityRowConverter<ServiceEntity> {

    public static final Logger LOGGER = LoggerFactory.getLogger(ServiceEntityRowConverter.class);

    @Override
    public ServiceEntity execute(ResultSet resultSet) {
        try {
            Connection connection = resultSet.getStatement().getConnection();

            var currencyEntity = DaoFactory.getInstance()
                    .getCurrencyDao()
                    .findOneById(resultSet.getInt("currency_id"), connection)
                    .orElseThrow(() -> new EntityNotFoundException("Не вдалось отримати об'єкт сутності"
                                                                   + " CurrencyEntity, так як вона відсутня в"
                                                                   + " базі даних."));

            var serviceEntity = ServiceEntity.builder()
                    .id(resultSet.getInt("id"))
                    .name(resultSet.getString("name"))
                    .description(resultSet.getString("description"))
                    .photo(resultSet.getBytes("photo"))
                    .currencyEntity(currencyEntity)
                    .price(new Money(resultSet.getInt("price_whole_part"), resultSet.getInt("price_decimal_part")))
                    .build();
            setExtraFieldOnManyToMany(resultSet, serviceEntity);
            LOGGER.info("Результат конвертації ResultSet в об'єктний тип: {}", serviceEntity);
            return serviceEntity;
        } catch (SQLException e) {
            String message = "Не вдалось отримати ResultSet в %s. Детально: %s"
                    .formatted(ServiceEntityRowConverter.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new NoResultException(message);
        }
    }

    // не найкраща практика?
    private static void setExtraFieldOnManyToMany(ResultSet resultSet, ServiceEntity serviceEntity)
            throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            if (metaData.getColumnLabel(i).equals("extra_description")) {
                serviceEntity.setExtraFieldDescription(resultSet.getString("extra_description"));
                break;
            }
        }
    }

    private ServiceEntityRowConverter() {}

    private static class SingletonHolder {
        public static final ServiceEntityRowConverter INSTANCE = new ServiceEntityRowConverter();
    }

    public static ServiceEntityRowConverter getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
