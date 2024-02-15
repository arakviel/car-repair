package com.arakviel.carrepair.domain.proxy;

import com.arakviel.carrepair.domain.impl.Employee;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.persistence.entity.proxy.EmployeeEntitiesProxy;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class StaffProxy implements Staff {

    private Staff staff;

    @Override
    public List<Employee> get(UUID orderId) {
        if (Objects.isNull(staff)) {
            var employeeEntitiesProxy = new EmployeeEntitiesProxy();
            staff = oi -> employeeEntitiesProxy.get(orderId).stream()
                    .map(ee -> MapperFactory.getInstance().getEmployeeMapper().toDomain(ee))
                    .toList();
        }
        return staff.get(orderId);
    }
}
