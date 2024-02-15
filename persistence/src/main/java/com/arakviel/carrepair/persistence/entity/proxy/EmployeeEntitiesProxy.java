package com.arakviel.carrepair.persistence.entity.proxy;

import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.entity.impl.EmployeeEntity;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EmployeeEntitiesProxy implements EmployeeEntities {

    private EmployeeEntities employeeEntities;

    @Override
    public List<EmployeeEntity> get(UUID orderEntityId) {
        if (Objects.isNull(employeeEntities)) {
            employeeEntities = oi -> Collections.unmodifiableList(
                    DaoFactory.getInstance().getOrderDao().findAllStaff(oi));
        }
        return employeeEntities.get(orderEntityId);
    }
}
