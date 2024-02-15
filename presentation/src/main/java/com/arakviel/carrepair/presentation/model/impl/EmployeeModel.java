package com.arakviel.carrepair.presentation.model.impl;

import com.arakviel.carrepair.presentation.model.BaseModel;
import com.arakviel.carrepair.presentation.model.Model;
import com.arakviel.carrepair.presentation.model.proxy.OrderModels;
import com.arakviel.carrepair.presentation.model.proxy.OrderModelsProxy;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class EmployeeModel extends BaseModel<UUID> implements Model, Cloneable {

    private ObjectProperty<AddressModel> addressModel = new SimpleObjectProperty<>();
    private ObjectProperty<WorkroomModel> workroomModel = new SimpleObjectProperty<>();
    private ObjectProperty<PositionModel> positionModel = new SimpleObjectProperty<>();
    private StringProperty firstName = new SimpleStringProperty();
    private StringProperty lastName = new SimpleStringProperty();
    private StringProperty middleName = new SimpleStringProperty();
    private ObjectProperty<Image> photo = new SimpleObjectProperty<>();
    private ObjectProperty<byte[]> passportDocCopy = new SimpleObjectProperty<>();
    private ObjectProperty<byte[]> bankNumberDocCopy = new SimpleObjectProperty<>();
    private ObjectProperty<byte[]> otherDocCopy = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private ObjectProperty<UserModel> userModel = new SimpleObjectProperty<>();
    private OrderModels orderModels;

    private EmployeeModel(
            AddressModel addressModel,
            WorkroomModel workroomModel,
            PositionModel positionModel,
            String firstName,
            String lastName,
            Image photo,
            byte[] passportDocCopy) {
        this.id = new SimpleObjectProperty<>();
        this.addressModel.set(addressModel);
        this.workroomModel.set(workroomModel);
        this.positionModel.set(positionModel);
        this.firstName.set(firstName);
        this.lastName.set(lastName);
        this.photo.set(photo);
        this.passportDocCopy.set(passportDocCopy);
        this.orderModels = new OrderModelsProxy();
    }

    public static EmployeeModelBuilderAddress builder() {
        return addressModel -> workroomModel ->
                positionModel -> firstName -> lastName -> photo -> passportDocCopy -> () -> new EmployeeModel(
                        addressModel, workroomModel, positionModel, firstName, lastName, photo, passportDocCopy);
    }

    @FunctionalInterface
    public interface EmployeeModelBuilderAddress {
        EmployeeModelBuilderWorkroom addressModel(AddressModel addressModel);
    }

    @FunctionalInterface
    public interface EmployeeModelBuilderWorkroom {
        EmployeeModelBuilderPosition workroomModel(WorkroomModel workroomModel);
    }

    @FunctionalInterface
    public interface EmployeeModelBuilderPosition {
        EmployeeModelBuilderFirstName positionModel(PositionModel positionModel);
    }

    @FunctionalInterface
    public interface EmployeeModelBuilderFirstName {
        EmployeeModelBuilderLastName firstName(String firstName);
    }

    @FunctionalInterface
    public interface EmployeeModelBuilderLastName {
        EmployeeModelBuilderPhoto lastName(String lastName);
    }

    @FunctionalInterface
    public interface EmployeeModelBuilderPhoto {
        EmployeePassportDocCopy photo(Image photo);
    }

    @FunctionalInterface
    public interface EmployeePassportDocCopy {
        EmployeeModelBuilder passportDocCopy(byte[] passportDocCopy);
    }

    @FunctionalInterface
    public interface EmployeeModelBuilder {
        EmployeeModel build();
    }

    public AddressModel getAddressModel() {
        return addressModel.get();
    }

    public ObjectProperty<AddressModel> addressModelProperty() {
        return addressModel;
    }

    public void setAddressModel(AddressModel addressModel) {
        this.addressModel.set(addressModel);
    }

    public WorkroomModel getWorkroomModel() {
        return workroomModel.get();
    }

    public ObjectProperty<WorkroomModel> workroomModelProperty() {
        return workroomModel;
    }

    public void setWorkroomModel(WorkroomModel workroomModel) {
        this.workroomModel.set(workroomModel);
    }

    public PositionModel getPositionModel() {
        return positionModel.get();
    }

    public ObjectProperty<PositionModel> positionModelProperty() {
        return positionModel;
    }

    public void setPositionModel(PositionModel positionModel) {
        this.positionModel.set(positionModel);
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

    public byte[] getPassportDocCopy() {
        return passportDocCopy.get();
    }

    public ObjectProperty<byte[]> passportDocCopyProperty() {
        return passportDocCopy;
    }

    public void setPassportDocCopy(byte[] passportDocCopy) {
        this.passportDocCopy.set(passportDocCopy);
    }

    public byte[] getBankNumberDocCopy() {
        return bankNumberDocCopy.get();
    }

    public ObjectProperty<byte[]> bankNumberDocCopyProperty() {
        return bankNumberDocCopy;
    }

    public void setBankNumberDocCopy(byte[] bankNumberDocCopy) {
        this.bankNumberDocCopy.set(bankNumberDocCopy);
    }

    public byte[] getOtherDocCopy() {
        return otherDocCopy.get();
    }

    public ObjectProperty<byte[]> otherDocCopyProperty() {
        return otherDocCopy;
    }

    public void setOtherDocCopy(byte[] otherDocCopy) {
        this.otherDocCopy.set(otherDocCopy);
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

    public UserModel getUserModel() {
        return userModel.get();
    }

    public ObjectProperty<UserModel> userModelProperty() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel.set(userModel);
    }

    public OrderModels getOrderModelsProxy() {
        return orderModels;
    }

    public List<OrderModel> getOrderModels() {
        return orderModels.get(id.get());
    }

    public void setOrderModels(OrderModels orderModelsProxy) {
        this.orderModels = orderModelsProxy;
    }

    @Override
    public EmployeeModel clone() {
        try {
            EmployeeModel cloned = (EmployeeModel) super.clone();
            cloned.id = new SimpleObjectProperty<>(this.id.get());
            cloned.addressModel = new SimpleObjectProperty<>(this.addressModel.get());
            cloned.workroomModel = new SimpleObjectProperty<>(this.workroomModel.get());
            cloned.positionModel = new SimpleObjectProperty<>(this.positionModel.get());
            cloned.firstName = new SimpleStringProperty(this.firstName.get());
            cloned.lastName = new SimpleStringProperty(this.lastName.get());
            cloned.middleName = new SimpleStringProperty(this.middleName.get());
            cloned.photo = new SimpleObjectProperty<>(this.photo.get());
            cloned.passportDocCopy = new SimpleObjectProperty<>(this.passportDocCopy.get());
            cloned.bankNumberDocCopy = new SimpleObjectProperty<>(this.bankNumberDocCopy.get());
            cloned.otherDocCopy = new SimpleObjectProperty<>(this.otherDocCopy.get());
            cloned.updatedAt = new SimpleObjectProperty<>(this.updatedAt.get());
            cloned.createdAt = new SimpleObjectProperty<>(this.createdAt.get());
            cloned.userModel = new SimpleObjectProperty<>(this.userModel.get());

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public String toString() {
        return "%d %s %s %s %s"
                .formatted(
                        getPositionModel().getSalaryPerHour().wholePart(),
                        getFirstName(),
                        getLastName(),
                        getMiddleName(),
                        getWorkroomModel().getName());
    }
}
