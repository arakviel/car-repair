package com.arakviel.carrepair.presentation.model.proxy;

import com.arakviel.carrepair.domain.proxy.ServicesProxy;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.model.impl.ServiceModel;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ServiceModelsProxy implements ServiceModels {

    private ServiceModels services;

    @Override
    public List<ServiceModel> get(UUID orderModelId) {
        if (Objects.isNull(services)) {
            var servicesProxy = new ServicesProxy();
            services = oi -> servicesProxy.get(oi).stream()
                    .map(s -> ModelMapperFactory.getInstance()
                            .getServiceModelMapper()
                            .toModel(s))
                    .toList();
        }
        return services.get(orderModelId);
    }
}
