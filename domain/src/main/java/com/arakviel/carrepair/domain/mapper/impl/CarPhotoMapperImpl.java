package com.arakviel.carrepair.domain.mapper.impl;

import com.arakviel.carrepair.domain.impl.CarPhoto;
import com.arakviel.carrepair.domain.mapper.CarPhotoMapper;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.persistence.entity.impl.CarPhotoEntity;

public class CarPhotoMapperImpl implements CarPhotoMapper {

    @Override
    public CarPhoto toDomain(CarPhotoEntity entity) {
        var carPhoto = CarPhoto.builder()
                .car(MapperFactory.getInstance().getCarMapper().toDomain(entity.getCarEntity()))
                .photo(entity.getPhoto())
                .build();
        carPhoto.setId(entity.getId());
        carPhoto.setDescription(entity.getDescription());
        return carPhoto;
    }

    @Override
    public CarPhotoEntity toEntity(CarPhoto domain) {
        var carPhotoEntity = CarPhotoEntity.builder()
                .id(domain.getId())
                .carEntity(MapperFactory.getInstance().getCarMapper().toEntity(domain.getCar()))
                .photo(domain.getPhoto())
                .build();
        carPhotoEntity.setId(domain.getId());
        carPhotoEntity.setDescription(domain.getDescription());
        return carPhotoEntity;
    }

    private CarPhotoMapperImpl() {}

    private static class SingletonHolder {
        public static final CarPhotoMapperImpl INSTANCE = new CarPhotoMapperImpl();
    }

    public static CarPhotoMapperImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
