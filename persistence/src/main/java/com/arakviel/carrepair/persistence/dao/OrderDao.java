package com.arakviel.carrepair.persistence.dao;

import com.arakviel.carrepair.persistence.entity.impl.EmployeeEntity;
import com.arakviel.carrepair.persistence.entity.impl.OrderEntity;
import com.arakviel.carrepair.persistence.entity.impl.ServiceEntity;
import com.arakviel.carrepair.persistence.entity.impl.SpareEntity;
import com.arakviel.carrepair.persistence.entity.impl.WorkroomEntity;
import com.arakviel.carrepair.persistence.filter.impl.OrderFilterDto;
import java.util.List;
import java.util.UUID;

public interface OrderDao extends GenericDao<UUID, OrderEntity, OrderFilterDto> {

    List<OrderEntity> findAllByWorkroomEntity(WorkroomEntity workroomEntity);

    List<OrderEntity> findAllByWorkroomEntity(OrderFilterDto filter, WorkroomEntity workroomEntity);

    List<EmployeeEntity> findAllStaff(UUID orderId);

    void attachToEmployee(UUID orderId, UUID employeeId);

    void detachFromEmployee(UUID orderId, UUID employeeId);

    List<ServiceEntity> findAllServices(UUID orderId);

    String findServiceOrderDescription(UUID orderId, Integer serviceId);

    void attachToService(UUID orderId, Integer serviceId, String description);

    void detachFromService(UUID orderId, Integer serviceId);

    List<SpareEntity> findAllSpares(UUID orderId);

    int findSpareOrderQuantity(UUID orderId, UUID spareId);

    void attachToSpare(UUID orderId, UUID spareId, int quantity);

    void detachFromSpare(UUID orderId, UUID spareId);
}
