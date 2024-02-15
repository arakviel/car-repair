package com.arakviel.carrepair.persistence.entity.impl;

import com.arakviel.carrepair.persistence.entity.BaseEntity;
import com.arakviel.carrepair.persistence.entity.Entity;
import java.util.StringJoiner;
import java.util.UUID;

public class SpareEntity extends BaseEntity<UUID> implements Entity {

    private WorkroomEntity workroomEntity;
    private String name;
    private String description;
    private byte[] photo;
    private Money price;
    private int quantityInStock;
    // не хороший варіант
    private int extraFieldQuantity;

    private SpareEntity(UUID id, WorkroomEntity workroomEntity, String name, Money price, int quantityInStock) {
        super(id);
        this.workroomEntity = workroomEntity;
        this.name = name;
        this.price = price;
        this.quantityInStock = quantityInStock;
    }

    public static SpareEntityBuilderId builder() {
        return id -> workroomEntity -> name ->
                price -> quantityInStock -> () -> new SpareEntity(id, workroomEntity, name, price, quantityInStock);
    }

    @FunctionalInterface
    public interface SpareEntityBuilderId {
        SpareEntityBuilderWorkroomEntity id(UUID id);
    }

    @FunctionalInterface
    public interface SpareEntityBuilderWorkroomEntity {
        SpareEntityBuilderName workroomEntity(WorkroomEntity workroomEntity);
    }

    @FunctionalInterface
    public interface SpareEntityBuilderName {
        SpareEntityBuilderPrice name(String name);
    }

    @FunctionalInterface
    public interface SpareEntityBuilderPrice {
        SpareEntityBuilderQuantityInStock price(Money price);
    }

    @FunctionalInterface
    public interface SpareEntityBuilderQuantityInStock {
        SpareEntityBuilder quantityInStock(int quantityInStock);
    }

    @FunctionalInterface
    public interface SpareEntityBuilder {
        SpareEntity build();
    }

    public WorkroomEntity getWorkroomEntity() {
        return workroomEntity;
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public int getExtraFieldQuantity() {
        return extraFieldQuantity;
    }

    public void setExtraFieldQuantity(int extraFieldQuantity) {
        this.extraFieldQuantity = extraFieldQuantity;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SpareEntity.class.getSimpleName() + "[", "]")
                .add("workroomEntity=" + workroomEntity)
                .add("name='" + name + "'")
                .add("description='" + description + "'")
                .add("photo=" + photo)
                .add("price=" + price)
                .add("quantityInStock=" + quantityInStock)
                .add("id=" + id)
                .toString();
    }
}
