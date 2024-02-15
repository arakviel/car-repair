package com.arakviel.carrepair.domain.proxy;

import com.arakviel.carrepair.domain.impl.Service;
import java.util.List;
import java.util.UUID;

@FunctionalInterface
public interface Services {

    List<Service> get(UUID orderId);
}
