package com.arakviel.carrepair.presentation.model.impl;

import com.arakviel.carrepair.presentation.model.BaseModel;
import com.arakviel.carrepair.presentation.model.Model;
import java.util.StringJoiner;
import java.util.UUID;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class CarPhotoModel extends BaseModel<UUID> implements Model, Cloneable {

    private ObjectProperty<CarModel> carModel = new SimpleObjectProperty<>();
    private ObjectProperty<Image> photo = new SimpleObjectProperty<>();
    private StringProperty description = new SimpleStringProperty();

    private CarPhotoModel(CarModel carModel, Image photo) {
        this.id = new SimpleObjectProperty<>();
        this.carModel.set(carModel);
        this.photo.set(photo);
    }

    public static CarPhotoModelBuilderCar builder() {
        return carModel -> photo -> () -> new CarPhotoModel(carModel, photo);
    }

    @FunctionalInterface
    public interface CarPhotoModelBuilderCar {
        CarPhotoModelBuilderPhoto carModel(CarModel carModel);
    }

    @FunctionalInterface
    public interface CarPhotoModelBuilderPhoto {
        CarPhotoModelBuilder photo(Image photo);
    }

    @FunctionalInterface
    public interface CarPhotoModelBuilder {
        CarPhotoModel build();
    }

    public CarModel getCarModel() {
        return carModel.get();
    }

    public ObjectProperty<CarModel> carModelProperty() {
        return carModel;
    }

    public void setCarModel(CarModel carModel) {
        this.carModel.set(carModel);
    }

    public Image getPhoto() {
        return photo.get();
    }

    public ObjectProperty<Image> photoProperty() {
        return photo;
    }

    public void setPhoto(Image photo) {
        this.photo.set(photo);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    @Override
    public CarPhotoModel clone() {
        try {
            CarPhotoModel cloned = (CarPhotoModel) super.clone();
            cloned.id = new SimpleObjectProperty<>(this.id.get());
            cloned.carModel = new SimpleObjectProperty<>(this.carModel.get());
            cloned.photo = new SimpleObjectProperty<>(this.photo.get());
            cloned.description = new SimpleStringProperty(this.description.get());

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CarPhotoModel.class.getSimpleName() + "[", "]")
                .add("carModel=" + carModel)
                .add("photo=" + photo)
                .add("description=" + description)
                .add("id=" + id)
                .toString();
    }
}
