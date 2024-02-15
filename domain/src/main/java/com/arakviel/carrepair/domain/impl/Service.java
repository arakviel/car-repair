package com.arakviel.carrepair.domain.impl;

import com.arakviel.carrepair.domain.BaseDomain;
import com.arakviel.carrepair.domain.Domain;
import java.util.StringJoiner;

public class Service extends BaseDomain<Integer> implements Domain {

    private String name;
    private String description;
    private byte[] photo;
    private Currency currency;
    private Money price;
    // не хороший варіант.
    private String extraFieldDescription;

    private Service(String name, String description, byte[] photo, Currency currency, Money price) {
        this.name = name;
        this.description = description;
        this.photo = photo;
        this.currency = currency;
        this.price = price;
    }

    public static ServiceBuilderName builder() {
        return name -> description ->
                photo -> currency -> price -> () -> new Service(name, description, photo, currency, price);
    }

    @FunctionalInterface
    public interface ServiceBuilderName {
        ServiceBuilderDescription name(String name);
    }

    @FunctionalInterface
    public interface ServiceBuilderDescription {
        ServiceBuilderPhoto description(String description);
    }

    @FunctionalInterface
    public interface ServiceBuilderPhoto {
        ServiceBuilderCurrency photo(byte[] photo);
    }

    @FunctionalInterface
    public interface ServiceBuilderCurrency {
        ServiceBuilderPrice currency(Currency currency);
    }

    @FunctionalInterface
    public interface ServiceBuilderPrice {
        ServiceBuilder price(Money price);
    }

    @FunctionalInterface
    public interface ServiceBuilder {
        Service build();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Money getPrice() {
        return price;
    }

    public String getExtraFieldDescription() {
        return extraFieldDescription;
    }

    public void setExtraFieldDescription(String extraFieldDescription) {
        this.extraFieldDescription = extraFieldDescription;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Service.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("description='" + description + "'")
                .add("photo=" + photo)
                .add("currency=" + currency)
                .add("price=" + price)
                .add("extraFieldDescription='" + extraFieldDescription + "'")
                .add("id=" + id)
                .toString();
    }
}
