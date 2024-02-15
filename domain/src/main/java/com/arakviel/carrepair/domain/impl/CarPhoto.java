package com.arakviel.carrepair.domain.impl;

import com.arakviel.carrepair.domain.BaseDomain;
import com.arakviel.carrepair.domain.Domain;
import java.util.StringJoiner;
import java.util.UUID;

public class CarPhoto extends BaseDomain<UUID> implements Domain {

    private Car car;
    private byte[] photo;
    private String description;

    private CarPhoto(Car car, byte[] photo) {
        this.car = car;
        this.photo = photo;
    }

    public static CarPhotoBuilderCar builder() {
        return car -> photo -> () -> new CarPhoto(car, photo);
    }

    @FunctionalInterface
    public interface CarPhotoBuilderCar {
        CarPhotoBuilderPhoto car(Car car);
    }

    @FunctionalInterface
    public interface CarPhotoBuilderPhoto {
        CarPhotoBuilder photo(byte[] photo);
    }

    @FunctionalInterface
    public interface CarPhotoBuilder {
        CarPhoto build();
    }

    public Car getCar() {
        return car;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CarPhoto.class.getSimpleName() + "[", "]")
                .add("car=" + car)
                .add("photo=" + photo)
                .add("description='" + description + "'")
                .add("id=" + id)
                .toString();
    }
}
