package com.arakviel.carrepair.presentation.model.impl;

import com.arakviel.carrepair.presentation.model.BaseModel;
import com.arakviel.carrepair.presentation.model.Model;
import java.util.UUID;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AddressModel extends BaseModel<UUID> implements Model, Cloneable {

    private StringProperty country = new SimpleStringProperty();
    private StringProperty region = new SimpleStringProperty();
    private StringProperty city = new SimpleStringProperty();
    private StringProperty street = new SimpleStringProperty();
    private StringProperty home = new SimpleStringProperty();

    private AddressModel(String country, String region, String city, String street, String home) {
        this.id = new SimpleObjectProperty<>();
        this.country.set(country);
        this.region.set(region);
        this.city.set(city);
        this.street.set(street);
        this.home.set(home);
    }

    public static AddressModelBuilderCountry builder() {
        return country ->
                region -> city -> street -> home -> () -> new AddressModel(country, region, city, street, home);
    }

    @FunctionalInterface
    public interface AddressModelBuilderCountry {
        AddressModelBuilderRegion country(String country);
    }

    @FunctionalInterface
    public interface AddressModelBuilderRegion {
        AddressModelBuilderCity region(String region);
    }

    @FunctionalInterface
    public interface AddressModelBuilderCity {
        AddressModelBuilderStreet city(String city);
    }

    @FunctionalInterface
    public interface AddressModelBuilderStreet {
        AddressModelBuilderHome street(String street);
    }

    @FunctionalInterface
    public interface AddressModelBuilderHome {
        AddressModelBuilder home(String home);
    }

    @FunctionalInterface
    public interface AddressModelBuilder {
        AddressModel build();
    }

    public String getCountry() {
        return country.get();
    }

    public StringProperty countryProperty() {
        return country;
    }

    public void setCountry(String country) {
        this.country.set(country);
    }

    public String getRegion() {
        return region.get();
    }

    public StringProperty regionProperty() {
        return region;
    }

    public void setRegion(String region) {
        this.region.set(region);
    }

    public String getCity() {
        return city.get();
    }

    public StringProperty cityProperty() {
        return city;
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    public String getStreet() {
        return street.get();
    }

    public StringProperty streetProperty() {
        return street;
    }

    public void setStreet(String street) {
        this.street.set(street);
    }

    public String getHome() {
        return home.get();
    }

    public StringProperty homeProperty() {
        return home;
    }

    public void setHome(String home) {
        this.home.set(home);
    }

    public String getFullAddress() {
        return "%s, %s, %s, %s, %s".formatted(country.get(), region.get(), city.get(), street.get(), home.get());
    }

    @Override
    public AddressModel clone() {
        try {
            AddressModel cloned = (AddressModel) super.clone();
            cloned.id = new SimpleObjectProperty<>(this.id.get());
            cloned.country = new SimpleStringProperty(this.country.get());
            cloned.region = new SimpleStringProperty(this.region.get());
            cloned.city = new SimpleStringProperty(this.city.get());
            cloned.street = new SimpleStringProperty(this.street.get());
            cloned.home = new SimpleStringProperty(this.home.get());

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public String toString() {
        return getFullAddress();
    }
}
