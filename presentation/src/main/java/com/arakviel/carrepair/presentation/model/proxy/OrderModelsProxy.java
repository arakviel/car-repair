package com.arakviel.carrepair.presentation.model.proxy;

import com.arakviel.carrepair.domain.proxy.OrdersProxy;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.model.impl.OrderModel;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class OrderModelsProxy implements OrderModels {

    private OrderModels orderModels;

    @Override
    public List<OrderModel> get(UUID employeeModelId) {
        if (Objects.isNull(orderModels)) {
            var ordersProxy = new OrdersProxy();
            orderModels = ei -> ordersProxy.get(ei).stream()
                    .map(o -> ModelMapperFactory.getInstance()
                            .getOrderModelMapper()
                            .toModel(o))
                    .toList();
        }
        return orderModels.get(employeeModelId);
    }
}
