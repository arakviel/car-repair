package com.arakviel.carrepair.persistence.dao;

import com.arakviel.carrepair.persistence.entity.impl.OrderEntity;
import com.arakviel.carrepair.persistence.entity.impl.SpareEntity;
import com.arakviel.carrepair.persistence.filter.impl.SpareFilterDto;
import java.util.List;
import java.util.UUID;

public interface SpareDao extends GenericDao<UUID, SpareEntity, SpareFilterDto> {

    List<OrderEntity> findAllOrders(UUID spareId);

    void attachToOrder(UUID spareId, UUID orderId, int quantity);

    void detachFromOrder(UUID spareId, UUID orderId);
}
