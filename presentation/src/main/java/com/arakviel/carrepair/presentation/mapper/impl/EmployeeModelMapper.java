package com.arakviel.carrepair.presentation.mapper.impl;

import com.arakviel.carrepair.domain.impl.Employee;
import com.arakviel.carrepair.presentation.mapper.ModelMapper;
import com.arakviel.carrepair.presentation.mapper.util.PhotoImageToByteConverter;
import com.arakviel.carrepair.presentation.model.impl.EmployeeModel;
import java.io.ByteArrayInputStream;
import java.util.Objects;
import javafx.scene.image.Image;

public class EmployeeModelMapper implements ModelMapper<Employee, EmployeeModel> {

    @Override
    public EmployeeModel toModel(Employee employee) {
        var modelMapperFactory = ModelMapperFactory.getInstance();
        var addressModelMapper = modelMapperFactory.getAddressModelMapper();
        var workroomModelMapper = modelMapperFactory.getWorkroomModelMapper();
        var positionModelMapper = modelMapperFactory.getPositionModelMapper();

        Image photo = null;
        if (Objects.nonNull(employee.getPhoto())) {
            if (employee.getPhoto().length != 0) {
                photo = new Image(new ByteArrayInputStream(employee.getPhoto()));
            }
        }

        EmployeeModel employeeModel = EmployeeModel.builder()
                .addressModel(addressModelMapper.toModel(employee.getAddress()))
                .workroomModel(workroomModelMapper.toModel(employee.getWorkroom()))
                .positionModel(positionModelMapper.toModel(employee.getPosition()))
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .photo(photo)
                .passportDocCopy(employee.getPassportDocCopy())
                .build();
        employeeModel.setId(employee.getId());
        employeeModel.setMiddleName(employee.getMiddleName());
        employeeModel.setBankNumberDocCopy(employee.getBankNumberDocCopy());
        employeeModel.setOtherDocCopy(employee.getOtherDocCopy());
        employeeModel.setUpdatedAt(employee.getUpdatedAt());
        employeeModel.setCreatedAt(employee.getCreatedAt());
        return employeeModel;
    }

    @Override
    public Employee toDomain(EmployeeModel employeeModel) {
        var modelMapperFactory = ModelMapperFactory.getInstance();
        var addressModelMapper = modelMapperFactory.getAddressModelMapper();
        var workroomModelMapper = modelMapperFactory.getWorkroomModelMapper();
        var positionModelMapper = modelMapperFactory.getPositionModelMapper();

        byte[] photo = null;
        if (Objects.nonNull(employeeModel.getPhoto())) {
            photo = PhotoImageToByteConverter.convert(employeeModel.getPhoto());
        }

        Employee employee = Employee.builder()
                .address(addressModelMapper.toDomain(employeeModel.getAddressModel()))
                .workroom(workroomModelMapper.toDomain(employeeModel.getWorkroomModel()))
                .position(positionModelMapper.toDomain(employeeModel.getPositionModel()))
                .firstName(employeeModel.getFirstName())
                .lastName(employeeModel.getLastName())
                .photo(photo)
                .passportDocCopy(employeeModel.getPassportDocCopy())
                .build();
        employee.setId(employeeModel.getId());
        employee.setMiddleName(employeeModel.getMiddleName());
        employee.setBankNumberDocCopy(employeeModel.getBankNumberDocCopy());
        employee.setOtherDocCopy(employeeModel.getOtherDocCopy());
        employee.setUpdatedAt(employeeModel.getUpdatedAt());
        employee.setCreatedAt(employeeModel.getCreatedAt());
        return employee;
    }

    EmployeeModelMapper() {}
}
