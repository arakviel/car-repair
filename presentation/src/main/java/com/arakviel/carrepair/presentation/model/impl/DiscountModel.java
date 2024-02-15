package com.arakviel.carrepair.presentation.model.impl;

import com.arakviel.carrepair.presentation.model.BaseModel;
import com.arakviel.carrepair.presentation.model.Model;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DiscountModel extends BaseModel<Integer> implements Model, Cloneable {

    private StringProperty name = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    private IntegerProperty value = new SimpleIntegerProperty();

    private DiscountModel(String name, short value) {
        this.id = new SimpleObjectProperty<>();
        this.name.set(name);
        this.value.set(value);
    }

    public static DiscountModelBuilderName builder() {
        return name -> value -> () -> new DiscountModel(name, value);
    }

    @FunctionalInterface
    public interface DiscountModelBuilderName {
        DiscountModelBuilderValue name(String name);
    }

    @FunctionalInterface
    public interface DiscountModelBuilderValue {
        DiscountModelBuilder value(short value);
    }

    @FunctionalInterface
    public interface DiscountModelBuilder {
        DiscountModel build();
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

    public int getValue() {
        return value.get();
    }

    public IntegerProperty valueProperty() {
        return value;
    }

    public void setValue(int value) {
        this.value.set(value);
    }

    @Override
    public DiscountModel clone() {
        try {
            DiscountModel cloned = (DiscountModel) super.clone();
            cloned.id = new SimpleObjectProperty<>(this.id.get());
            cloned.name = new SimpleStringProperty(this.name.get());
            cloned.description = new SimpleStringProperty(this.description.get());
            cloned.value = new SimpleIntegerProperty(this.value.get());

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public String toString() {
        return getName();
    }
}
