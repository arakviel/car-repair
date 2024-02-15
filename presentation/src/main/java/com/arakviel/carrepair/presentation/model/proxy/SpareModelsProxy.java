package com.arakviel.carrepair.presentation.model.proxy;

import com.arakviel.carrepair.domain.proxy.SparesProxy;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.model.impl.SpareModel;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SpareModelsProxy implements SpareModels {

    private SpareModels spares;

    @Override
    public List<SpareModel> get(UUID orderModelId) {
        if (Objects.isNull(spares)) {
            var sparesProxy = new SparesProxy();
            spares = oi -> sparesProxy.get(oi).stream()
                    .map(s -> ModelMapperFactory.getInstance()
                            .getSpareModelMapper()
                            .toModel(s))
                    .toList();
        }
        return spares.get(orderModelId);
    }
}
