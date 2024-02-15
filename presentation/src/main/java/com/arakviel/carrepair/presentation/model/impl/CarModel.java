package com.arakviel.carrepair.presentation.model.impl;

import com.arakviel.carrepair.presentation.model.BaseModel;
import com.arakviel.carrepair.presentation.model.Model;
import java.time.LocalDateTime;
import java.util.UUID;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class CarModel extends BaseModel<UUID> implements Model, Cloneable {

    private ObjectProperty<ModelModel> modelModel = new SimpleObjectProperty<>();
    private StringProperty number = new SimpleStringProperty();
    private IntegerProperty year = new SimpleIntegerProperty();
    private ObjectProperty<EngineType> engineType = new SimpleObjectProperty<>();
    private IntegerProperty mileage = new SimpleIntegerProperty();
    private ObjectProperty<Color> color = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();

    private CarModel(ModelModel modelModel, String number, short year, EngineType engineType, int mileage) {
        this.id = new SimpleObjectProperty<>();
        this.modelModel.set(modelModel);
        this.number.set(number);
        this.year.set(year);
        this.engineType.set(engineType);
        this.mileage.set(mileage);
    }

    public static CarModelBuilderModel builder() {
        return modelModel -> number ->
                year -> engineType -> mileage -> () -> new CarModel(modelModel, number, year, engineType, mileage);
    }

    @FunctionalInterface
    public interface CarModelBuilderModel {
        CarModelBuilderNumber modelModel(ModelModel modelModel);
    }

    @FunctionalInterface
    public interface CarModelBuilderNumber {
        CarModelBuilderYear number(String number);
    }

    @FunctionalInterface
    public interface CarModelBuilderYear {
        CarModelBuilderEngineType year(short year);
    }

    @FunctionalInterface
    public interface CarModelBuilderEngineType {
        CarModelBuilderMileage engineType(EngineType engineType);
    }

    @FunctionalInterface
    public interface CarModelBuilderMileage {
        CarModelBuilder mileage(int mileage);
    }

    @FunctionalInterface
    public interface CarModelBuilder {
        CarModel build();
    }

    public ModelModel getModelModel() {
        return modelModel.get();
    }

    public ObjectProperty<ModelModel> modelModelProperty() {
        return modelModel;
    }

    public void setModelModel(ModelModel modelModel) {
        this.modelModel.set(modelModel);
    }

    public String getNumber() {
        return number.get();
    }

    public StringProperty numberProperty() {
        return number;
    }

    public void setNumber(String number) {
        this.number.set(number);
    }

    public int getYear() {
        return year.get();
    }

    public IntegerProperty yearProperty() {
        return year;
    }

    public void setYear(int year) {
        this.year.set(year);
    }

    public EngineType getEngineType() {
        return engineType.get();
    }

    public ObjectProperty<EngineType> engineTypeProperty() {
        return engineType;
    }

    public void setEngineType(EngineType engineType) {
        this.engineType.set(engineType);
    }

    public int getMileage() {
        return mileage.get();
    }

    public IntegerProperty mileageProperty() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage.set(mileage);
    }

    public Color getColor() {
        return color.get();
    }

    public String getHexColor() {
        Color innerColor = color.get();
        // Отримання компонентів кольору
        int red = (int) (innerColor.getRed() * 255);
        int green = (int) (innerColor.getGreen() * 255);
        int blue = (int) (innerColor.getBlue() * 255);

        // Перетворення компонентів у шістнадцятковий формат
        String redHex = String.format("%02x", red);
        String greenHex = String.format("%02x", green);
        String blueHex = String.format("%02x", blue);

        // Формування HEX-представлення кольору
        return "#" + redHex + greenHex + blueHex;
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt.get();
    }

    public ObjectProperty<LocalDateTime> updatedAtProperty() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt.set(updatedAt);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }

    public ObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt.set(createdAt);
    }

    @Override
    public CarModel clone() {
        try {
            CarModel cloned = (CarModel) super.clone();
            cloned.id = new SimpleObjectProperty<>(this.id.get());
            cloned.modelModel = new SimpleObjectProperty<>(this.modelModel.get());
            cloned.number = new SimpleStringProperty(this.number.get());
            cloned.year = new SimpleIntegerProperty(this.year.get());
            cloned.engineType = new SimpleObjectProperty<>(this.engineType.get());
            cloned.mileage = new SimpleIntegerProperty(this.mileage.get());
            cloned.color = new SimpleObjectProperty<>(this.color.get());
            cloned.updatedAt = new SimpleObjectProperty<>(this.updatedAt.get());
            cloned.createdAt = new SimpleObjectProperty<>(this.createdAt.get());

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public String toString() {
        return "%s %s".formatted(getNumber(), getModelModel());
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
