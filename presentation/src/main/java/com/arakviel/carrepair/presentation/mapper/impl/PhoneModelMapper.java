package com.arakviel.carrepair.presentation.mapper.impl;

import com.arakviel.carrepair.domain.impl.Phone;
import com.arakviel.carrepair.domain.impl.Phone.PhoneType;
import com.arakviel.carrepair.presentation.mapper.ModelMapper;
import com.arakviel.carrepair.presentation.model.impl.PhoneModel;

public class PhoneModelMapper implements ModelMapper<Phone, PhoneModel> {

    @Override
    public PhoneModel toModel(Phone phone) {
        var employeeModelMapper = ModelMapperFactory.getInstance().getEmployeeModelMapper();
        PhoneModel phoneModel = PhoneModel.builder()
                .employeeModel(employeeModelMapper.toModel(phone.getEmployee()))
                .phoneType(PhoneModel.PhoneType.valueOf(phone.getPhoneType().toString()))
                .value(phone.getValue())
                .build();
        phoneModel.setId(phone.getId());
        return phoneModel;
    }

    @Override
    public Phone toDomain(PhoneModel phoneModel) {
        var employeeModelMapper = ModelMapperFactory.getInstance().getEmployeeModelMapper();
        Phone phone = Phone.builder()
                .employee(employeeModelMapper.toDomain(phoneModel.getEmployeeModel()))
                .phoneType(PhoneType.valueOf(phoneModel.getPhoneType().toString()))
                .value(phoneModel.getValue())
                .build();
        phone.setId(phoneModel.getId());
        return phone;
    }

    PhoneModelMapper() {}
}
