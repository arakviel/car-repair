package com.arakviel.carrepair.presentation.model.impl;

import com.arakviel.carrepair.presentation.model.BaseModel;
import com.arakviel.carrepair.presentation.model.Model;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ModelModel extends BaseModel<Integer> implements Model, Cloneable {

    private ObjectProperty<BrandModel> brandModel = new SimpleObjectProperty<>();
    private StringProperty name = new SimpleStringProperty();

    private ModelModel(BrandModel brandModel, String name) {
        this.id = new SimpleObjectProperty<>();
        this.brandModel.set(brandModel);
        this.name.set(name);
    }

    public static ModelModelBuilderBrand builder() {
        return brandModel -> name -> () -> new ModelModel(brandModel, name);
    }

    @FunctionalInterface
    public interface ModelModelBuilderBrand {
        ModelModelBuilderName brandModel(BrandModel brandModel);
    }

    @FunctionalInterface
    public interface ModelModelBuilderName {
        ModelModelBuilder name(String name);
    }

    @FunctionalInterface
    public interface ModelModelBuilder {
        ModelModel build();
    }

    public BrandModel getBrandModel() {
        return brandModel.get();
    }

    public ObjectProperty<BrandModel> brandModelProperty() {
        return brandModel;
    }

    public void setBrandModel(BrandModel brandModel) {
        this.brandModel.set(brandModel);
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
    public ModelModel clone() {
        try {
            ModelModel cloned = (ModelModel) super.clone();
            cloned.id = new SimpleObjectProperty<>(this.id.get());
            cloned.brandModel = new SimpleObjectProperty<>(this.brandModel.get());
            cloned.name = new SimpleStringProperty(this.name.get());

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public String toString() {
        return "%s %s".formatted(brandModel.get().getName(), name.get());
    }
}
