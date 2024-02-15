package com.arakviel.carrepair.persistence.impl.converter.impl;

import com.arakviel.carrepair.persistence.entity.impl.ClientEntity;
import com.arakviel.carrepair.persistence.exception.persistance.NoResultException;
import com.arakviel.carrepair.persistence.impl.converter.EntityRowConverter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientEntityRowConverter implements EntityRowConverter<ClientEntity> {

    public static final Logger LOGGER = LoggerFactory.getLogger(ClientEntityRowConverter.class);

    @Override
    public ClientEntity execute(ResultSet resultSet) {
        try {
            var clientEntity = ClientEntity.builder()
                    .id(UUID.fromString(resultSet.getString("id")))
                    .phone(resultSet.getString("phone"))
                    .email(resultSet.getString("email"))
                    .build();
            clientEntity.setFirstName(resultSet.getObject("first_name", String.class));
            clientEntity.setLastName(resultSet.getObject("last_name", String.class));
            clientEntity.setMiddleName(resultSet.getObject("middle_name", String.class));
            clientEntity.setPhoto(resultSet.getBytes("photo"));
            clientEntity.setUpdatedAt(resultSet.getObject("updated_at", LocalDateTime.class));
            clientEntity.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));

            LOGGER.info("Результат конвертації ResultSet в об'єктний тип: {}", clientEntity);
            return clientEntity;
        } catch (SQLException e) {
            String message = "Не вдалось отримати ResultSet в %s. Детально: %s"
                    .formatted(ClientEntityRowConverter.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new NoResultException(message);
        }
    }

    private ClientEntityRowConverter() {}

    private static class SingletonHolder {
        public static final ClientEntityRowConverter INSTANCE = new ClientEntityRowConverter();
    }

    public static ClientEntityRowConverter getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
