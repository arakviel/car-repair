package com.arakviel.carrepair.domain.proxy;

import com.arakviel.carrepair.domain.impl.Employee;
import java.util.List;
import java.util.UUID;

@FunctionalInterface
public interface Staff {
    List<Employee> get(UUID orderId);
}
