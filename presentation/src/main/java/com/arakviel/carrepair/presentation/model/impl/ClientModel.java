package com.arakviel.carrepair.presentation.model.impl;

import com.arakviel.carrepair.presentation.model.BaseModel;
import com.arakviel.carrepair.presentation.model.Model;
import java.time.LocalDateTime;
import java.util.UUID;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class ClientModel extends BaseModel<UUID> implements Model, Cloneable {

    private StringProperty phone = new SimpleStringProperty();
    private StringProperty email = new SimpleStringProperty();
    private StringProperty firstName = new SimpleStringProperty();
    private StringProperty lastName = new SimpleStringProperty();
    private StringProperty middleName = new SimpleStringProperty();
    private StringProperty fullName = new SimpleStringProperty();
    private ObjectProperty<Image> photo = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();

    private ClientModel(String phone, String email) {
        this.id = new SimpleObjectProperty<>();
        this.phone.set(phone);
        this.email.set(email);
        this.fullName = fullNameProperty();
    }

    public static ClientModelBuilderPhone builder() {
        return phone -> email -> () -> new ClientModel(phone, email);
    }

    @FunctionalInterface
    public interface ClientModelBuilderPhone {
        ClientModelBuilderEmail phone(String phone);
    }

    @FunctionalInterface
    public interface ClientModelBuilderEmail {
        ClientModelBuilder email(String email);
    }

    @FunctionalInterface
    public interface ClientModelBuilder {
        ClientModel build();
    }

    public String getPhone() {
        return phone.get();
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getMiddleName() {
        return middleName.get();
    }

    public StringProperty middleNameProperty() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName.set(middleName);
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

    public String getFullName() {
        return fullNameProperty().get();
    }

    private StringProperty fullNameProperty() {
        fullName.bind(Bindings.createStringBinding(
                () -> {
                    String firstNameValue = firstName.get();
                    String lastNameValue = lastName.get();
                    String middleNameValue = middleName.get();
                    if (middleNameValue == null) {
                        middleNameValue = "";
                    }
                    return firstNameValue + " " + lastNameValue + " " + middleNameValue;
                },
                firstName,
                lastName,
                middleName));
        return fullName;
    }

    @Override
    public ClientModel clone() {
        try {
            ClientModel cloned = (ClientModel) super.clone();
            cloned.id = new SimpleObjectProperty<>(this.id.get());
            cloned.phone = new SimpleStringProperty(this.phone.get());
            cloned.email = new SimpleStringProperty(this.email.get());
            cloned.firstName = new SimpleStringProperty(this.firstName.get());
            cloned.lastName = new SimpleStringProperty(this.lastName.get());
            cloned.middleName = new SimpleStringProperty(this.middleName.get());
            cloned.fullName = new SimpleStringProperty(this.fullName.get());
            cloned.photo = new SimpleObjectProperty<>(this.photo.get());
            cloned.updatedAt = new SimpleObjectProperty<>(this.updatedAt.get());
            cloned.createdAt = new SimpleObjectProperty<>(this.createdAt.get());

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
