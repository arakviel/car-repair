package com.arakviel.carrepair.presentation.model.proxy;

import com.arakviel.carrepair.presentation.model.impl.OrderModel;
import java.util.List;
import java.util.UUID;

@FunctionalInterface
public interface OrderModels {

    List<OrderModel> get(UUID employeeModelId);
}
