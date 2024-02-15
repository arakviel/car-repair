package com.arakviel.carrepair.domain.proxy;

import com.arakviel.carrepair.domain.impl.Service;
import com.arakviel.carrepair.domain.mapper.impl.ServiceMapperImpl;
import com.arakviel.carrepair.persistence.entity.proxy.ServiceEntitiesProxy;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ServicesProxy implements Services {

    private Services services;

    @Override
    public List<Service> get(UUID orderId) {
        if (Objects.isNull(services)) {
            var serviceEntitiesProxy = new ServiceEntitiesProxy();
            services = oi -> serviceEntitiesProxy.get(oi).stream()
                    .map(se -> ServiceMapperImpl.getInstance().toDomain(se))
                    .toList();
        }
        return services.get(orderId);
    }
}
