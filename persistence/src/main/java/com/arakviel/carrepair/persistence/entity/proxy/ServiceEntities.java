package com.arakviel.carrepair.persistence.entity.proxy;

import com.arakviel.carrepair.persistence.entity.impl.ServiceEntity;
import java.util.List;
import java.util.UUID;

@FunctionalInterface
public interface ServiceEntities {
    List<ServiceEntity> get(UUID orderEntityId);
}
