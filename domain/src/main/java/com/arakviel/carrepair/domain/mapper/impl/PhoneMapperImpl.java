package com.arakviel.carrepair.domain.mapper.impl;

import com.arakviel.carrepair.domain.impl.Phone;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.domain.mapper.PhoneMapper;
import com.arakviel.carrepair.persistence.entity.impl.PhoneEntity;

public class PhoneMapperImpl implements PhoneMapper {

    @Override
    public Phone toDomain(PhoneEntity entity) {
        var phone = Phone.builder()
                .employee(MapperFactory.getInstance().getEmployeeMapper().toDomain(entity.getEmployeeEntity()))
                .phoneType(Phone.PhoneType.valueOf(entity.getPhoneType().toString()))
                .value(entity.getValue())
                .build();
        phone.setId(entity.getId());
        return phone;
    }

    @Override
    public PhoneEntity toEntity(Phone domain) {
        return PhoneEntity.builder()
                .id(domain.getId())
                .employeeEntity(MapperFactory.getInstance().getEmployeeMapper().toEntity(domain.getEmployee()))
                .phoneType(PhoneEntity.PhoneType.valueOf(domain.getPhoneType().toString()))
                .value(domain.getValue())
                .build();
    }

    private PhoneMapperImpl() {}

    private static class SingletonHolder {
        public static final PhoneMapperImpl INSTANCE = new PhoneMapperImpl();
    }

    public static PhoneMapperImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
