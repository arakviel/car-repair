package com.arakviel.carrepair.presentation.model.proxy;

import com.arakviel.carrepair.presentation.model.impl.ServiceModel;
import java.util.List;
import java.util.UUID;

@FunctionalInterface
public interface ServiceModels {

    List<ServiceModel> get(UUID orderModelId);
}
