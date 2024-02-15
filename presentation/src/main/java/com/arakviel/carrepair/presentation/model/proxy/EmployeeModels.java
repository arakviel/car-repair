package com.arakviel.carrepair.presentation.model.proxy;

import com.arakviel.carrepair.presentation.model.impl.EmployeeModel;
import java.util.List;
import java.util.UUID;

@FunctionalInterface
public interface EmployeeModels {
    List<EmployeeModel> get(UUID orderModelId);
}
