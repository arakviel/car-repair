package com.arakviel.carrepair.persistence.entity.impl;

import com.arakviel.carrepair.persistence.entity.BaseEntity;
import com.arakviel.carrepair.persistence.entity.Entity;
import java.util.StringJoiner;
import java.util.UUID;

public class CarPhotoEntity extends BaseEntity<UUID> implements Entity {

    private CarEntity carEntity;
    private byte[] photo;
    private String description;

    private CarPhotoEntity(UUID id, CarEntity carEntity, byte[] photo) {
        super(id);
        this.carEntity = carEntity;
        this.photo = photo;
    }

    public static CarPhotoEntityBuilderId builder() {
        return id -> carEntity -> photo -> () -> new CarPhotoEntity(id, carEntity, photo);
    }

    @FunctionalInterface
    public interface CarPhotoEntityBuilderId {
        CarPhotoEntityBuilderCarEntity id(UUID id);
    }

    @FunctionalInterface
    public interface CarPhotoEntityBuilderCarEntity {
        CarPhotoEntityBuilderPhoto carEntity(CarEntity carEntity);
    }

    @FunctionalInterface
    public interface CarPhotoEntityBuilderPhoto {
        CarPhotoEntityBuilder photo(byte[] photo);
    }

    @FunctionalInterface
    public interface CarPhotoEntityBuilder {
        CarPhotoEntity build();
    }

    public CarEntity getCarEntity() {
        return carEntity;
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
        return new StringJoiner(", ", CarPhotoEntity.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("carEntity=" + carEntity)
                .add("photo=" + photo)
                .add("description='" + description + "'")
                .toString();
    }
}
