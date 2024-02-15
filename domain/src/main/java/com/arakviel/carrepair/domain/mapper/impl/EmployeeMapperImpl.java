package com.arakviel.carrepair.domain.mapper.impl;

import com.arakviel.carrepair.domain.impl.Employee;
import com.arakviel.carrepair.domain.mapper.EmployeeMapper;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.persistence.entity.impl.EmployeeEntity;

public class EmployeeMapperImpl implements EmployeeMapper {

    @Override
    public Employee toDomain(EmployeeEntity entity) {
        var employee = Employee.builder()
                .address(MapperFactory.getInstance().getAddressMapper().toDomain(entity.getAddressEntity()))
                .workroom(MapperFactory.getInstance().getWorkroomMapper().toDomain(entity.getWorkroomEntity()))
                .position(MapperFactory.getInstance().getPositionMapper().toDomain(entity.getPositionEntity()))
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .photo(entity.getPhoto())
                .passportDocCopy(entity.getPassportDocCopy())
                .build();
        employee.setId(entity.getId());
        employee.setMiddleName(entity.getMiddleName());
        employee.setBankNumberDocCopy(entity.getBankNumberDocCopy());
        employee.setOtherDocCopy(entity.getOtherDocCopy());
        employee.setUpdatedAt(entity.getUpdatedAt());
        employee.setCreatedAt(entity.getCreatedAt());
        return employee;
    }

    @Override
    public EmployeeEntity toEntity(Employee domain) {
        var employeeEntity = EmployeeEntity.builder()
                .id(domain.getId())
                .addressEntity(MapperFactory.getInstance().getAddressMapper().toEntity(domain.getAddress()))
                .workroomEntity(MapperFactory.getInstance().getWorkroomMapper().toEntity(domain.getWorkroom()))
                .positionEntity(MapperFactory.getInstance().getPositionMapper().toEntity(domain.getPosition()))
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .photo(domain.getPhoto())
                .passportDocCopy(domain.getPassportDocCopy())
                .build();
        employeeEntity.setMiddleName(domain.getMiddleName());
        employeeEntity.setBankNumberDocCopy(domain.getBankNumberDocCopy());
        employeeEntity.setOtherDocCopy(domain.getOtherDocCopy());
        employeeEntity.setUpdatedAt(domain.getUpdatedAt());
        employeeEntity.setCreatedAt(domain.getCreatedAt());
        return employeeEntity;
    }

    private EmployeeMapperImpl() {}

    private static class SingletonHolder {
        public static final EmployeeMapperImpl INSTANCE = new EmployeeMapperImpl();
    }

    public static EmployeeMapperImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
