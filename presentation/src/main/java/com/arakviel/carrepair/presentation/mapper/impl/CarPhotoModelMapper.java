package com.arakviel.carrepair.presentation.mapper.impl;

import com.arakviel.carrepair.domain.impl.CarPhoto;
import com.arakviel.carrepair.presentation.mapper.ModelMapper;
import com.arakviel.carrepair.presentation.mapper.util.PhotoImageToByteConverter;
import com.arakviel.carrepair.presentation.model.impl.CarPhotoModel;
import java.io.ByteArrayInputStream;
import java.util.Objects;
import javafx.scene.image.Image;

public class CarPhotoModelMapper implements ModelMapper<CarPhoto, CarPhotoModel> {

    @Override
    public CarPhotoModel toModel(CarPhoto carPhoto) {
        var carModelMapper = ModelMapperFactory.getInstance().getCarModelMapper();
        CarPhotoModel carPhotoModel = CarPhotoModel.builder()
                .carModel(carModelMapper.toModel(carPhoto.getCar()))
                .photo(new Image(new ByteArrayInputStream(carPhoto.getPhoto())))
                .build();
        carPhotoModel.setId(carPhoto.getId());
        carPhotoModel.setDescription(carPhoto.getDescription());
        return carPhotoModel;
    }

    @Override
    public CarPhoto toDomain(CarPhotoModel carPhotoModel) {
        var carModelMapper = ModelMapperFactory.getInstance().getCarModelMapper();

        byte[] photo = null;
        if (Objects.nonNull(carPhotoModel.getPhoto())) {
            photo = PhotoImageToByteConverter.convert(carPhotoModel.getPhoto());
        }

        CarPhoto carPhoto = CarPhoto.builder()
                .car(carModelMapper.toDomain(carPhotoModel.getCarModel()))
                .photo(photo)
                .build();
        carPhoto.setId(carPhotoModel.getId());
        carPhoto.setDescription(carPhotoModel.getDescription());
        return carPhoto;
    }
}
