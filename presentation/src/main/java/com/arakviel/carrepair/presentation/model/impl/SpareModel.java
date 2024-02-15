package com.arakviel.carrepair.presentation.model.impl;

import com.arakviel.carrepair.presentation.model.BaseModel;
import com.arakviel.carrepair.presentation.model.Model;
import java.util.Objects;
import java.util.UUID;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class SpareModel extends BaseModel<UUID> implements Model, Cloneable {

    private ObjectProperty<WorkroomModel> workroomModel = new SimpleObjectProperty<>();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    private ObjectProperty<Image> photo = new SimpleObjectProperty<>();
    private ObjectProperty<MoneyModel> price = new SimpleObjectProperty<>();
    private IntegerProperty quantityInStock = new SimpleIntegerProperty();
    private IntegerProperty extraFieldQuantity = new SimpleIntegerProperty();

    private SpareModel(WorkroomModel workroomModel, String name, MoneyModel price, int quantityInStock) {
        this.id = new SimpleObjectProperty<>();
        this.workroomModel.set(workroomModel);
        this.name.set(name);
        this.price.set(price);
        this.quantityInStock.set(quantityInStock);
    }

    public static SpareModelBuilderWorkroom builder() {
        return workroomModel ->
                name -> price -> quantityInStock -> () -> new SpareModel(workroomModel, name, price, quantityInStock);
    }

    @FunctionalInterface
    public interface SpareModelBuilderWorkroom {
        SpareModelBuilderName workroom(WorkroomModel workroomModel);
    }

    @FunctionalInterface
    public interface SpareModelBuilderName {
        SpareModelBuilderPrice name(String name);
    }

    @FunctionalInterface
    public interface SpareModelBuilderPrice {
        SpareModelBuilderQuantityInStock price(MoneyModel price);
    }

    @FunctionalInterface
    public interface SpareModelBuilderQuantityInStock {
        SpareModelBuilder quantityInStock(int quantityInStock);
    }

    @FunctionalInterface
    public interface SpareModelBuilder {
        SpareModel build();
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

    public Image getPhoto() {
        return photo.get();
    }

    public ObjectProperty<Image> photoProperty() {
        return photo;
    }

    public void setPhoto(Image photo) {
        this.photo.set(photo);
    }

    public MoneyModel getPrice() {
        return price.get();
    }

    public ObjectProperty<MoneyModel> priceProperty() {
        return price;
    }

    public void setPrice(MoneyModel price) {
        this.price.set(price);
    }

    public int getQuantityInStock() {
        return quantityInStock.get();
    }

    public IntegerProperty quantityInStockProperty() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock.set(quantityInStock);
    }

    public int getExtraFieldQuantity() {
        return extraFieldQuantity.get();
    }

    public IntegerProperty extraFieldQuantityProperty() {
        return extraFieldQuantity;
    }

    public void setExtraFieldQuantity(int extraFieldQuantity) {
        this.extraFieldQuantity.set(extraFieldQuantity);
    }

    @Override
    public SpareModel clone() {
        try {
            SpareModel cloned = (SpareModel) super.clone();
            cloned.id = new SimpleObjectProperty<>(this.id.get());
            cloned.workroomModel = new SimpleObjectProperty<>(this.workroomModel.get());
            cloned.name = new SimpleStringProperty(this.name.get());
            cloned.description = new SimpleStringProperty(this.description.get());
            cloned.photo = new SimpleObjectProperty<>(this.photo.get());
            cloned.price = new SimpleObjectProperty<>(this.price.get());
            cloned.quantityInStock = new SimpleIntegerProperty(this.quantityInStock.get());
            cloned.extraFieldQuantity = new SimpleIntegerProperty(this.extraFieldQuantity.get());

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public String toString() {
        String toString = "";
        if (Objects.nonNull(getExtraFieldQuantity())) {
            toString = "%s - %s шт.".formatted(getName(), getExtraFieldQuantity());
        } else {
            toString = getName();
        }
        return toString;
    }
}
