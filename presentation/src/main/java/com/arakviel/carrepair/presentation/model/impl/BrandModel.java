package com.arakviel.carrepair.presentation.model.impl;

import com.arakviel.carrepair.presentation.model.BaseModel;
import com.arakviel.carrepair.presentation.model.Model;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BrandModel extends BaseModel<Integer> implements Model, Cloneable {

    private StringProperty name = new SimpleStringProperty();

    private BrandModel(String name) {
        this.id = new SimpleObjectProperty<>();
        this.name.set(name);
    }

    public static BrandModelBuilderName builder() {
        return name -> () -> new BrandModel(name);
    }

    @FunctionalInterface
    public interface BrandModelBuilderName {
        BrandModelBuilder name(String name);
    }

    @FunctionalInterface
    public interface BrandModelBuilder {
        BrandModel build();
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

    @Override
    public BrandModel clone() {
        try {
            BrandModel cloned = (BrandModel) super.clone();
            cloned.id = new SimpleObjectProperty<>(this.id.get());
            cloned.name = new SimpleStringProperty(this.name.get());

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
