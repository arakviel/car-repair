package com.arakviel.carrepair.presentation.model.proxy;

import com.arakviel.carrepair.presentation.model.impl.SpareModel;
import java.util.List;
import java.util.UUID;

@FunctionalInterface
public interface SpareModels {

    List<SpareModel> get(UUID orderModelId);
}
