package com.arakviel.carrepair.persistence.dao;

import com.arakviel.carrepair.persistence.entity.impl.EmployeeEntity;
import com.arakviel.carrepair.persistence.entity.impl.OrderEntity;
import com.arakviel.carrepair.persistence.filter.impl.EmployeeFilterDto;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeDao extends GenericDao<UUID, EmployeeEntity, EmployeeFilterDto> {

    /**
     * Get an entity object by identifier.
     *
     * @param id primary key identifier
     * @param connection JDBC Connection
     * @return Optional<EmployeeEntity>
     */
    Optional<EmployeeEntity> findOneById(UUID id, Connection connection);

    List<OrderEntity> findAllOrders(UUID employeeId);

    void attachToOrder(UUID employeeId, UUID orderId);

    void detachFromOrder(UUID employeeId, UUID orderId);
}
