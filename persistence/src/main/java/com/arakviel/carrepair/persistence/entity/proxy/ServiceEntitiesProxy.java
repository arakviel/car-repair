package com.arakviel.carrepair.persistence.entity.proxy;

import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.entity.impl.ServiceEntity;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ServiceEntitiesProxy implements ServiceEntities {

    private ServiceEntities serviceEntities;

    @Override
    public List<ServiceEntity> get(UUID orderId) {
        if (Objects.isNull(serviceEntities)) {
            serviceEntities = oi -> Collections.unmodifiableList(
                    DaoFactory.getInstance().getOrderDao().findAllServices(oi));
        }
        return serviceEntities.get(orderId);
    }
}
