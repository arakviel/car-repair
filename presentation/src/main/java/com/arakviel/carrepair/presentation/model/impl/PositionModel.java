package com.arakviel.carrepair.presentation.model.impl;

import com.arakviel.carrepair.presentation.model.BaseModel;
import com.arakviel.carrepair.presentation.model.Model;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PositionModel extends BaseModel<Integer> implements Model, Cloneable {

    private StringProperty name = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    private ObjectProperty<CurrencyModel> currencyModel = new SimpleObjectProperty<>();
    private ObjectProperty<MoneyModel> salaryPerHour = new SimpleObjectProperty<>();
    private ObjectProperty<RoleModel> roleModel = new SimpleObjectProperty<>();

    private PositionModel(String name, CurrencyModel currencyModel, MoneyModel salaryPerHour) {
        this.id = new SimpleObjectProperty<>();
        this.name.set(name);
        this.currencyModel.set(currencyModel);
        this.salaryPerHour.set(salaryPerHour);
    }

    public static PositionModelBuilderName builder() {
        return name -> currencyModel -> salaryPerHour -> () -> new PositionModel(name, currencyModel, salaryPerHour);
    }

    @FunctionalInterface
    public interface PositionModelBuilderName {

        PositionModelBuilderCurrency name(String name);
    }

    @FunctionalInterface
    public interface PositionModelBuilderCurrency {

        PositionModelBuilderSalaryPerHour currencyModel(CurrencyModel currencyModel);
    }

    @FunctionalInterface
    public interface PositionModelBuilderSalaryPerHour {
        PositionModelBuilder salaryPerHour(MoneyModel salaryPerHour);
    }

    @FunctionalInterface
    public interface PositionModelBuilder {
        PositionModel build();
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

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public CurrencyModel getCurrencyModel() {
        return currencyModel.get();
    }

    public ObjectProperty<CurrencyModel> currencyModelProperty() {
        return currencyModel;
    }

    public void setCurrencyModel(CurrencyModel currencyModel) {
        this.currencyModel.set(currencyModel);
    }

    public MoneyModel getSalaryPerHour() {
        return salaryPerHour.get();
    }

    public ObjectProperty<MoneyModel> salaryPerHourProperty() {
        return salaryPerHour;
    }

    public void setSalaryPerHour(MoneyModel salaryPerHour) {
        this.salaryPerHour.set(salaryPerHour);
    }

    public RoleModel getRoleModel() {
        return roleModel.get();
    }

    public ObjectProperty<RoleModel> roleModelProperty() {
        return roleModel;
    }

    public void setRoleModel(RoleModel roleModel) {
        this.roleModel.set(roleModel);
    }

    @Override
    public PositionModel clone() {
        try {
            PositionModel cloned = (PositionModel) super.clone();
            cloned.id = new SimpleObjectProperty<>(this.id.get());
            cloned.name = new SimpleStringProperty(this.name.get());
            cloned.description = new SimpleStringProperty(this.description.get());
            cloned.currencyModel = new SimpleObjectProperty<>(this.currencyModel.get());
            cloned.salaryPerHour = new SimpleObjectProperty<>(this.salaryPerHour.get());
            cloned.roleModel = new SimpleObjectProperty<>(this.roleModel.get());

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public String toString() {
        return name.get();
    }
}
