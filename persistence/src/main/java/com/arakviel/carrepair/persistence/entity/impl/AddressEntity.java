package com.arakviel.carrepair.persistence.entity.impl;

import com.arakviel.carrepair.persistence.entity.BaseEntity;
import com.arakviel.carrepair.persistence.entity.Entity;
import java.util.StringJoiner;
import java.util.UUID;

public class AddressEntity extends BaseEntity<UUID> implements Entity {

    private String country;
    private String region;
    private String city;
    private String street;
    private String home;

    private AddressEntity(UUID id, String country, String region, String city, String street, String home) {
        super(id);
        this.country = country;
        this.region = region;
        this.city = city;
        this.street = street;
        this.home = home;
    }

    public static AddressEntityBuilderId builder() {
        return id -> country -> region -> city -> street -> home -> () -> new AddressEntity(id, country, region, city, street, home);
    }

    @FunctionalInterface
    public interface AddressEntityBuilderId {
        AddressEntityBuilderCountry id(UUID id);
    }

    @FunctionalInterface
    public interface AddressEntityBuilderCountry {
        AddressEntityBuilderRegion country(String country);
    }

    @FunctionalInterface
    public interface AddressEntityBuilderRegion {
        AddressEntityBuilderCity region(String region);
    }

    @FunctionalInterface
    public interface AddressEntityBuilderCity {
        AddressEntityBuilderStreet city(String city);
    }

    @FunctionalInterface
    public interface AddressEntityBuilderStreet {
        AddressEntityBuilderHome street(String street);
    }

    @FunctionalInterface
    public interface AddressEntityBuilderHome {
        AddressEntityBuilder home(String home);
    }

    @FunctionalInterface
    public interface AddressEntityBuilder {
        AddressEntity build();
    }

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getHome() {
        return home;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AddressEntity.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("country='" + country + "'")
                .add("region='" + region + "'")
                .add("city='" + city + "'")
                .add("street='" + street + "'")
                .add("home='" + home + "'")
                .toString();
    }
}
