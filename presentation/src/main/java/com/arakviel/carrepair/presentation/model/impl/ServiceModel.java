package com.arakviel.carrepair.presentation.model.impl;

import com.arakviel.carrepair.presentation.model.BaseModel;
import com.arakviel.carrepair.presentation.model.Model;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class ServiceModel extends BaseModel<Integer> implements Model, Cloneable {

    private StringProperty name = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    private ObjectProperty<Image> photo = new SimpleObjectProperty<>();
    private ObjectProperty<CurrencyModel> currencyModel = new SimpleObjectProperty<>();
    private ObjectProperty<MoneyModel> price = new SimpleObjectProperty<>();
    private StringProperty extraFieldDescription = new SimpleStringProperty();

    private ServiceModel(String name, String description, Image photo, CurrencyModel currencyModel, MoneyModel price) {
        this.id = new SimpleObjectProperty<>();
        this.name.set(name);
        this.description.set(description);
        this.photo.set(photo);
        this.currencyModel.set(currencyModel);
        this.price.set(price);
    }

    public static ServiceModelBuilderName builder() {
        return name -> description -> photo ->
                currencyModel -> price -> () -> new ServiceModel(name, description, photo, currencyModel, price);
    }

    @FunctionalInterface
    public interface ServiceModelBuilderName {
        ServiceModelBuilderDescription name(String name);
    }

    @FunctionalInterface
    public interface ServiceModelBuilderDescription {
        ServiceModelBuilderPhoto description(String description);
    }

    @FunctionalInterface
    public interface ServiceModelBuilderPhoto {
        ServiceModelBuilderCurrency photo(Image photo);
    }

    @FunctionalInterface
    public interface ServiceModelBuilderCurrency {
        ServiceModelBuilderPrice currency(CurrencyModel currencyModel);
    }

    @FunctionalInterface
    public interface ServiceModelBuilderPrice {
        ServiceModelBuilder price(MoneyModel price);
    }

    @FunctionalInterface
    public interface ServiceModelBuilder {
        ServiceModel build();
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

    public CurrencyModel getCurrencyModel() {
        return currencyModel.get();
    }

    public ObjectProperty<CurrencyModel> currencyModelProperty() {
        return currencyModel;
    }

    public void setCurrencyModel(CurrencyModel currencyModel) {
        this.currencyModel.set(currencyModel);
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

    public String getExtraFieldDescription() {
        return extraFieldDescription.get();
    }

    public StringProperty extraFieldDescriptionProperty() {
        return extraFieldDescription;
    }

    public void setExtraFieldDescription(String extraFieldDescription) {
        this.extraFieldDescription.set(extraFieldDescription);
    }

    @Override
    public ServiceModel clone() {
        try {
            ServiceModel cloned = (ServiceModel) super.clone();
            cloned.id = new SimpleObjectProperty<>(this.id.get());
            cloned.name = new SimpleStringProperty(this.name.get());
            cloned.description = new SimpleStringProperty(this.description.get());
            cloned.photo = new SimpleObjectProperty<>(this.photo.get());
            cloned.currencyModel = new SimpleObjectProperty<>(this.currencyModel.get());
            cloned.price = new SimpleObjectProperty<>(this.price.get());
            cloned.extraFieldDescription = new SimpleStringProperty(this.extraFieldDescription.get());

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
