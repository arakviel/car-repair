package com.arakviel.carrepair.domain.impl;

import com.arakviel.carrepair.domain.BaseDomain;
import com.arakviel.carrepair.domain.Domain;
import java.awt.Color;
import java.time.LocalDateTime;
import java.util.UUID;

public class Car extends BaseDomain<UUID> implements Domain {

    private Model model;
    private String number;
    private short year;
    private EngineType engineType;
    private int mileage;
    private Color color;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    private Car(Model model, String number, short year, EngineType engineType, int mileage) {
        this.model = model;
        this.number = number;
        this.year = year;
        this.engineType = engineType;
        this.mileage = mileage;
    }

    public static CarBuilderModel builder() {
        return model ->
                number -> year -> engineType -> mileage -> () -> new Car(model, number, year, engineType, mileage);
    }

    @FunctionalInterface
    public interface CarBuilderModel {
        CarBuilderNumber model(Model model);
    }

    @FunctionalInterface
    public interface CarBuilderNumber {
        CarBuilderYear number(String number);
    }

    @FunctionalInterface
    public interface CarBuilderYear {
        CarBuilderEngineType year(short year);
    }

    @FunctionalInterface
    public interface CarBuilderEngineType {
        CarBuilderMileage engineType(EngineType engineType);
    }

    @FunctionalInterface
    public interface CarBuilderMileage {
        CarBuilder mileage(int mileage);
    }

    @FunctionalInterface
    public interface CarBuilder {
        Car build();
    }

    public Model getModel() {
        return model;
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
