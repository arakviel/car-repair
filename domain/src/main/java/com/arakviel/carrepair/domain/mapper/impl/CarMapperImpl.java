package com.arakviel.carrepair.domain.mapper.impl;

import com.arakviel.carrepair.domain.impl.Car;
import com.arakviel.carrepair.domain.mapper.CarMapper;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.persistence.entity.impl.CarEntity;

public class CarMapperImpl implements CarMapper {

    @Override
    public Car toDomain(CarEntity entity) {
        var car = Car.builder()
                .model(MapperFactory.getInstance().getModelMapper().toDomain(entity.getModelEntity()))
                .number(entity.getNumber())
                .year(entity.getYear())
                .engineType(Car.EngineType.valueOf(entity.getEngineType().toString()))
                .mileage(entity.getMileage())
                .build();
        car.setId(entity.getId());
        car.setColor(entity.getColor());
        car.setUpdatedAt(entity.getUpdatedAt());
        car.setCreatedAt(entity.getCreatedAt());
        return car;
    }

    @Override
    public CarEntity toEntity(Car domain) {
        var carEntity = CarEntity.builder()
                .id(domain.getId())
                .modelEntity(MapperFactory.getInstance().getModelMapper().toEntity(domain.getModel()))
                .number(domain.getNumber())
                .year(domain.getYear())
                .engineType(CarEntity.EngineType.valueOf(domain.getEngineType().toString()))
                .mileage(domain.getMileage())
                .build();
        carEntity.setColor(domain.getColor());
        carEntity.setUpdatedAt(domain.getUpdatedAt());
        carEntity.setCreatedAt(domain.getCreatedAt());
        return carEntity;
    }

    private CarMapperImpl() {}

    private static class SingletonHolder {
        public static final CarMapperImpl INSTANCE = new CarMapperImpl();
    }

    public static CarMapperImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
