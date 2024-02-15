package com.arakviel.carrepair.domain.proxy;

import com.arakviel.carrepair.domain.impl.Order;
import java.util.List;
import java.util.UUID;

@FunctionalInterface
public interface Orders {

    List<Order> get(UUID employeeId);
}
