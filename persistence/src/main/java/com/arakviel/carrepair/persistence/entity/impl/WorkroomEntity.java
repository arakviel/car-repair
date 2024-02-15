package com.arakviel.carrepair.persistence.entity.impl;

import com.arakviel.carrepair.persistence.entity.BaseEntity;
import com.arakviel.carrepair.persistence.entity.Entity;
import java.util.StringJoiner;
import java.util.UUID;

public class WorkroomEntity extends BaseEntity<UUID> implements Entity {

    private AddressEntity addressEntity;
    private String name;
    private byte[] photo;
    private String description;

    private WorkroomEntity(UUID id, AddressEntity addressEntity, String name, byte[] photo) {
        super(id);
        this.addressEntity = addressEntity;
        this.name = name;
        this.photo = photo;
    }

    public static WorkroomEntityBuilderId builder() {
        return id -> addressEntity -> name -> photo -> () -> new WorkroomEntity(id, addressEntity, name, photo);
    }

    @FunctionalInterface
    public interface WorkroomEntityBuilderId {
        WorkroomEntityBuilderAddressEntity id(UUID id);
    }

    @FunctionalInterface
    public interface WorkroomEntityBuilderAddressEntity {
        WorkroomEntityBuilderName addressEntity(AddressEntity addressEntity);
    }

    @FunctionalInterface
    public interface WorkroomEntityBuilderName {
        WorkroomEntityBuilderPhoto name(String name);
    }

    @FunctionalInterface
    public interface WorkroomEntityBuilderPhoto {
        WorkroomEntityBuilder photo(byte[] photo);
    }

    @FunctionalInterface
    public interface WorkroomEntityBuilder {
        WorkroomEntity build();
    }

    public AddressEntity getAddressEntity() {
        return addressEntity;
    }

    public String getName() {
        return name;
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
        return new StringJoiner(", ", WorkroomEntity.class.getSimpleName() + "[", "]")
                .add("addressEntity=" + addressEntity)
                .add("name='" + name + "'")
                .add("photo=" + photo)
                .add("description='" + description + "'")
                .add("id=" + id)
                .toString();
    }
}
