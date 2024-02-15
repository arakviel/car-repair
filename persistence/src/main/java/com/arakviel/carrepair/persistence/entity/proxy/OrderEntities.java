package com.arakviel.carrepair.persistence.entity.proxy;

import com.arakviel.carrepair.persistence.entity.impl.OrderEntity;
import java.util.List;
import java.util.UUID;

@FunctionalInterface
public interface OrderEntities {
    List<OrderEntity> get(UUID employeeEntityId);
}
