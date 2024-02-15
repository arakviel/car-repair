package com.arakviel.carrepair.domain.mapper.impl;

import com.arakviel.carrepair.domain.impl.Money;
import com.arakviel.carrepair.domain.impl.Service;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.domain.mapper.ServiceMapper;
import com.arakviel.carrepair.persistence.entity.impl.ServiceEntity;

public class ServiceMapperImpl implements ServiceMapper {

    @Override
    public Service toDomain(ServiceEntity entity) {
        var service = Service.builder()
                .name(entity.getName())
                .description(entity.getDescription())
                .photo(entity.getPhoto())
                .currency(MapperFactory.getInstance().getCurrencyMapper().toDomain(entity.getCurrencyEntity()))
                .price(new Money(
                        entity.getPrice().wholePart(), entity.getPrice().decimalPart()))
                .build();
        service.setId(entity.getId());
        service.setExtraFieldDescription(entity.getExtraFieldDescription());
        return service;
    }

    @Override
    public ServiceEntity toEntity(Service domain) {
        var serviceEntity = ServiceEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .photo(domain.getPhoto())
                .currencyEntity(MapperFactory.getInstance().getCurrencyMapper().toEntity(domain.getCurrency()))
                .price(new com.arakviel.carrepair.persistence.entity.impl.Money(
                        domain.getPrice().wholePart(), domain.getPrice().decimalPart()))
                .build();
        serviceEntity.setExtraFieldDescription(domain.getExtraFieldDescription());
        return serviceEntity;
    }

    private ServiceMapperImpl() {}

    private static class SingletonHolder {
        public static final ServiceMapperImpl INSTANCE = new ServiceMapperImpl();
    }

    public static ServiceMapperImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
