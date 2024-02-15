package com.arakviel.carrepair.presentation.model.impl;

import com.arakviel.carrepair.presentation.model.BaseModel;
import com.arakviel.carrepair.presentation.model.Model;
import java.util.StringJoiner;
import java.util.UUID;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PhoneModel extends BaseModel<UUID> implements Model, Cloneable {

    private ObjectProperty<EmployeeModel> employeeModel = new SimpleObjectProperty<>();
    private ObjectProperty<PhoneType> phoneType = new SimpleObjectProperty();
    private StringProperty value = new SimpleStringProperty();

    private PhoneModel(EmployeeModel employeeModel, PhoneType phoneType, String value) {
        this.id = new SimpleObjectProperty<>();
        this.employeeModel.set(employeeModel);
        this.phoneType.set(phoneType);
        this.value.set(value);
    }

    public static PhoneModelBuilderEmployee builder() {
        return employeeModel -> phoneType -> value -> () -> new PhoneModel(employeeModel, phoneType, value);
    }

    @FunctionalInterface
    public interface PhoneModelBuilderEmployee {
        PhoneModelBuilderPhoneType employeeModel(EmployeeModel employeeModel);
    }

    @FunctionalInterface
    public interface PhoneModelBuilderPhoneType {
        PhoneModelBuilderValue phoneType(PhoneType phoneType);
    }

    @FunctionalInterface
    public interface PhoneModelBuilderValue {
        PhoneModelBuilder value(String value);
    }

    @FunctionalInterface
    public interface PhoneModelBuilder {
        PhoneModel build();
    }

    public EmployeeModel getEmployeeModel() {
        return employeeModel.get();
    }

    public ObjectProperty<EmployeeModel> employeeModelProperty() {
        return employeeModel;
    }

    public void setEmployeeModel(EmployeeModel employeeModel) {
        this.employeeModel.set(employeeModel);
    }

    public PhoneType getPhoneType() {
        return phoneType.get();
    }

    public ObjectProperty<PhoneType> phoneTypeProperty() {
        return phoneType;
    }

    public void setPhoneType(PhoneType phoneType) {
        this.phoneType.set(phoneType);
    }

    public String getValue() {
        return value.get();
    }

    public StringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    @Override
    public PhoneModel clone() {
        try {
            PhoneModel cloned = (PhoneModel) super.clone();
            cloned.id = new SimpleObjectProperty<>(this.id.get());
            cloned.employeeModel = new SimpleObjectProperty<>(this.employeeModel.get());
            cloned.phoneType = new SimpleObjectProperty<>(this.phoneType.get());
            cloned.value = new SimpleStringProperty(this.value.get());

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PhoneModel.class.getSimpleName() + "[", "]")
                .add("employeeModel=" + employeeModel)
                .add("phoneType=" + phoneType)
                .add("value=" + value)
                .add("id=" + id)
                .toString();
    }

    public enum PhoneType {
        WORK("робочий"),
        PERSONAL("особистий");

        private final String type;

        PhoneType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
