package com.arakviel.carrepair.domain.proxy;

import com.arakviel.carrepair.domain.impl.Order;
import com.arakviel.carrepair.domain.mapper.impl.OrderMapperImpl;
import com.arakviel.carrepair.persistence.entity.proxy.OrderEntitiesProxy;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class OrdersProxy implements Orders {

    private Orders orders;

    @Override
    public List<Order> get(UUID employeeId) {
        if (Objects.isNull(orders)) {
            var orderEntitiesProxy = new OrderEntitiesProxy();
            orders = ei -> orderEntitiesProxy.get(ei).stream()
                    .map(oe -> OrderMapperImpl.getInstance().toDomain(oe))
                    .toList();
        }
        return orders.get(employeeId);
    }
}
