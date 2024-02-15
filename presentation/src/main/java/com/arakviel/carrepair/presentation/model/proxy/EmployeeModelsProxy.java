package com.arakviel.carrepair.presentation.model.proxy;

import com.arakviel.carrepair.domain.proxy.StaffProxy;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.model.impl.EmployeeModel;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EmployeeModelsProxy implements EmployeeModels {

    private EmployeeModels staffModel;

    @Override
    public List<EmployeeModel> get(UUID orderModelId) {
        if (Objects.isNull(staffModel)) {
            var staffProxy = new StaffProxy();
            staffModel = oi -> staffProxy.get(orderModelId).stream()
                    .map(e -> ModelMapperFactory.getInstance()
                            .getEmployeeModelMapper()
                            .toModel(e))
                    .toList();
        }
        return staffModel.get(orderModelId);
    }
}
