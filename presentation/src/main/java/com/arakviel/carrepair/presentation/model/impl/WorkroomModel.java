package com.arakviel.carrepair.presentation.model.impl;

import com.arakviel.carrepair.presentation.model.BaseModel;
import com.arakviel.carrepair.presentation.model.Model;
import java.util.UUID;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class WorkroomModel extends BaseModel<UUID> implements Model, Cloneable {

    private ObjectProperty<AddressModel> address = new SimpleObjectProperty<>();
    private StringProperty name = new SimpleStringProperty();
    private ObjectProperty<Image> photo = new SimpleObjectProperty<>();
    private StringProperty description = new SimpleStringProperty();

    public WorkroomModel(AddressModel address, String name, Image photo) {
        this.id = new SimpleObjectProperty<>();
        this.address.set(address);
        this.name.set(name);
        this.photo.set(photo);
    }

    public static WorkroomModelBuilderAddress builder() {
        return address -> name -> photo -> () -> new WorkroomModel(address, name, photo);
    }

    @FunctionalInterface
    public interface WorkroomModelBuilderAddress {
        WorkroomModelBuilderName address(AddressModel address);
    }

    @FunctionalInterface
    public interface WorkroomModelBuilderName {
        WorkroomModelBuilderPhoto name(String name);
    }

    @FunctionalInterface
    public interface WorkroomModelBuilderPhoto {
        WorkroomModelBuilder photo(Image photo);
    }

    @FunctionalInterface
    public interface WorkroomModelBuilder {
        WorkroomModel build();
    }

    public AddressModel getAddress() {
        return address.get();
    }

    public ObjectProperty<AddressModel> addressProperty() {
        return address;
    }

    public void setAddressModel(AddressModel address) {
        this.address.set(address);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
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
    public WorkroomModel clone() {
        try {
            WorkroomModel cloned = (WorkroomModel) super.clone();
            cloned.id = new SimpleObjectProperty<>(this.id.get());
            cloned.address = new SimpleObjectProperty<>(this.address.get());
            cloned.name = new SimpleStringProperty(this.name.get());
            cloned.photo = new SimpleObjectProperty<>(this.photo.get());
            cloned.description = new SimpleStringProperty(this.description.get());
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public String toString() {
        return "%s".formatted(name.get());
    }
}
