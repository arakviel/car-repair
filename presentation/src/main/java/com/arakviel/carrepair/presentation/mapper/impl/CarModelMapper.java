package com.arakviel.carrepair.presentation.mapper.impl;

import com.arakviel.carrepair.domain.impl.Car;
import com.arakviel.carrepair.presentation.mapper.ModelMapper;
import com.arakviel.carrepair.presentation.model.impl.CarModel;

public class CarModelMapper implements ModelMapper<Car, CarModel> {

    @Override
    public CarModel toModel(Car car) {
        var modelModelMapper = ModelMapperFactory.getInstance().getModelModelMapper();
        CarModel carModel = CarModel.builder()
                .modelModel(modelModelMapper.toModel(car.getModel()))
                .number(car.getNumber())
                .year(car.getYear())
                .engineType(CarModel.EngineType.valueOf(car.getEngineType().toString()))
                .mileage(car.getMileage())
                .build();
        carModel.setId(car.getId());
        carModel.setColor(convertToFX(car.getColor()));
        carModel.setUpdatedAt(car.getUpdatedAt());
        carModel.setCreatedAt(car.getCreatedAt());
        return carModel;
    }

    @Override
    public Car toDomain(CarModel carModel) {
        var modelModelMapper = ModelMapperFactory.getInstance().getModelModelMapper();
        Car car = Car.builder()
                .model(modelModelMapper.toDomain(carModel.getModelModel()))
                .number(carModel.getNumber())
                .year((short) carModel.getYear())
                .engineType(Car.EngineType.valueOf(carModel.getEngineType().toString()))
                .mileage(carModel.getMileage())
                .build();
        car.setId(carModel.getId());
        car.setColor(convertToAWT(carModel.getColor()));
        car.setUpdatedAt(carModel.getUpdatedAt());
        car.setCreatedAt(carModel.getCreatedAt());
        return car;
    }

    private javafx.scene.paint.Color convertToFX(java.awt.Color awtColor) {
        int red = awtColor.getRed();
        int green = awtColor.getGreen();
        int blue = awtColor.getBlue();
        int alpha = awtColor.getAlpha();

        double opacity = alpha / 255.0;

        return javafx.scene.paint.Color.rgb(red, green, blue, opacity);
    }

    private java.awt.Color convertToAWT(javafx.scene.paint.Color fxColor) {
        int red = (int) (fxColor.getRed() * 255);
        int green = (int) (fxColor.getGreen() * 255);
        int blue = (int) (fxColor.getBlue() * 255);
        int alpha = (int) (fxColor.getOpacity() * 255);

        return new java.awt.Color(red, green, blue, alpha);
    }
}
