package com.arakviel.carrepair.persistence.entity.impl;

import com.arakviel.carrepair.persistence.entity.BaseEntity;
import com.arakviel.carrepair.persistence.entity.Entity;
import java.awt.Color;
import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.UUID;

public class CarEntity extends BaseEntity<UUID> implements Entity {

    private ModelEntity modelEntity;
    private String number;
    private short year;
    private EngineType engineType;
    private int mileage;
    private Color color;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    private CarEntity(UUID id, ModelEntity modelEntity, String number, short year, EngineType engineType, int mileage) {
        super(id);
        this.modelEntity = modelEntity;
        this.number = number;
        this.year = year;
        this.engineType = engineType;
        this.mileage = mileage;
    }

    public static CarEntityBuilderId builder() {
        return id -> modelEntity -> number -> year ->
                engineType -> mileage -> () -> new CarEntity(id, modelEntity, number, year, engineType, mileage);
    }

    @FunctionalInterface
    public interface CarEntityBuilderId {
        CarEntityBuilderModelEntity id(UUID id);
    }

    @FunctionalInterface
    public interface CarEntityBuilderModelEntity {
        CarEntityBuilderNumber modelEntity(ModelEntity modelEntity);
    }

    @FunctionalInterface
    public interface CarEntityBuilderNumber {
        CarEntityBuilderYear number(String number);
    }

    @FunctionalInterface
    public interface CarEntityBuilderYear {
        CarEntityBuilderEngineType year(short year);
    }

    @FunctionalInterface
    public interface CarEntityBuilderEngineType {
        CarEntityBuilderMileage engineType(EngineType engineType);
    }

    @FunctionalInterface
    public interface CarEntityBuilderMileage {
        CarEntityBuilder mileage(int mileage);
    }

    @FunctionalInterface
    public interface CarEntityBuilder {
        CarEntity build();
    }

    public ModelEntity getModelEntity() {
        return modelEntity;
    }

    public String getNumber() {
        return number;
    }

    public short getYear() {
        return year;
    }

    public EngineType getEngineType() {
        return engineType;
    }

    public int getMileage() {
        return mileage;
    }

    public Color getColor() {
        return color;
    }

    public int toColorInt() {
        return color.getRGB();
    }

    public String toHexColorString() {
        return Integer.toHexString(color.getRGB()).substring(2).toUpperCase();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setColor(int hexColor) {
        this.color = new Color(hexColor);
    }

    public void setColor(String hexColor) {
        this.color = Color.decode("0x%s".formatted(hexColor));
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CarEntity.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("modelEntity=" + modelEntity)
                .add("number='" + number + "'")
                .add("year=" + year)
                .add("engineType=" + engineType)
                .add("mileage=" + mileage)
                .add("color=" + color)
                .add("updatedAt=" + updatedAt)
                .add("createdAt=" + createdAt)
                .toString();
    }

    public enum EngineType {
        BENZINE("бензиновий"),
        DIESEL("дизельний"),
        ELECTRIC("електричний"),
        HYBRID("гібридний"),
        GAS("газовий"),
        OTHER("інший");

        private final String name;

        EngineType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
