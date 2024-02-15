package com.arakviel.carrepair.persistence.dao;

import com.arakviel.carrepair.persistence.entity.impl.OrderEntity;
import com.arakviel.carrepair.persistence.entity.impl.ServiceEntity;
import com.arakviel.carrepair.persistence.filter.impl.ServiceFilterDto;
import java.util.List;
import java.util.UUID;

public interface ServiceDao extends GenericDao<Integer, ServiceEntity, ServiceFilterDto> {

    List<OrderEntity> findAllOrders(Integer serviceId);

    void attachToOrder(Integer serviceId, UUID orderId, String description);

    void detachFromOrder(Integer serviceId, UUID orderId);
}
