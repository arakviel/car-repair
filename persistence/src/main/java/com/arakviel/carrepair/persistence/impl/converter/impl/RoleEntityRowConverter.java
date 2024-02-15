package com.arakviel.carrepair.persistence.impl.converter.impl;

import com.arakviel.carrepair.persistence.entity.impl.RoleEntity;
import com.arakviel.carrepair.persistence.exception.persistance.NoResultException;
import com.arakviel.carrepair.persistence.impl.converter.EntityRowConverter;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoleEntityRowConverter implements EntityRowConverter<RoleEntity> {

    public static final Logger LOGGER = LoggerFactory.getLogger(RoleEntityRowConverter.class);

    @Override
    public RoleEntity execute(ResultSet resultSet) {
        try {
            var roleEntity = RoleEntity.builder()
                    .id(resultSet.getInt("position_id"))
                    .canEditUsers(resultSet.getBoolean("can_edit_users"))
                    .canEditSpares(resultSet.getBoolean("can_edit_spares"))
                    .canEditClients(resultSet.getBoolean("can_edit_clients"))
                    .canEditServices(resultSet.getBoolean("can_edit_services"))
                    .canEditOrders(resultSet.getBoolean("can_edit_orders"))
                    .canEditPayrolls(resultSet.getBoolean("can_edit_payrolls"))
                    .build();
            LOGGER.info("Результат конвертації ResultSet в об'єктний тип: {}", roleEntity);
            return roleEntity;
        } catch (SQLException e) {
            String message = "Не вдалось отримати ResultSet в %s. Детально: %s"
                    .formatted(RoleEntityRowConverter.class.getSimpleName(), e.getMessage());
            LOGGER.error(message);
            throw new NoResultException(message);
        }
    }

    private RoleEntityRowConverter() {}

    private static class SingletonHolder {
        public static final RoleEntityRowConverter INSTANCE = new RoleEntityRowConverter();
    }

    public static RoleEntityRowConverter getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
