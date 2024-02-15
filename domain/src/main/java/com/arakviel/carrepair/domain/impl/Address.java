package com.arakviel.carrepair.domain.impl;

import com.arakviel.carrepair.domain.BaseDomain;
import com.arakviel.carrepair.domain.Domain;
import java.util.StringJoiner;
import java.util.UUID;

public class Address extends BaseDomain<UUID> implements Domain {

    private String country;
    private String region;
    private String city;
    private String street;
    private String home;

    private Address(String country, String region, String city, String street, String home) {
        this.country = country;
        this.region = region;
        this.city = city;
        this.street = street;
        this.home = home;
    }

    public static AddressBuilderCountry builder() {
        return country -> region -> city -> street -> home -> () -> new Address(country, region, city, street, home);
    }

    @FunctionalInterface
    public interface AddressBuilderCountry {
        AddressBuilderRegion country(String country);
    }

    @FunctionalInterface
    public interface AddressBuilderRegion {
        AddressBuilderCity region(String region);
    }

    @FunctionalInterface
    public interface AddressBuilderCity {
        AddressBuilderStreet city(String city);
    }

    @FunctionalInterface
    public interface AddressBuilderStreet {
        AddressBuilderHome street(String street);
    }

    @FunctionalInterface
    public interface AddressBuilderHome {
        AddressBuilder home(String home);
    }

    @FunctionalInterface
    public interface AddressBuilder {
        Address build();
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

    public String getFullAddress() {
        return "%s, %s, %s, %s, %s".formatted(country, region, city, street, home);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Address.class.getSimpleName() + "[", "]")
                .add("country='" + country + "'")
                .add("region='" + region + "'")
                .add("city='" + city + "'")
                .add("street='" + street + "'")
                .add("home='" + home + "'")
                .add("id=" + id)
                .toString();
    }
}
