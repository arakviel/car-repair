package com.arakviel.carrepair.domain.proxy;

import com.arakviel.carrepair.domain.impl.Spare;
import java.util.List;
import java.util.UUID;

@FunctionalInterface
public interface Spares {

    List<Spare> get(UUID orderId);
}
