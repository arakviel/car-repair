package com.arakviel.carrepair.domain.impl;

import com.arakviel.carrepair.domain.BaseDomain;
import com.arakviel.carrepair.domain.Domain;
import java.util.StringJoiner;

public class Model extends BaseDomain<Integer> implements Domain {

    private Brand brand;
    private String name;

    private Model(Brand brand, String name) {
        this.brand = brand;
        this.name = name;
    }

    public static ModelBuilderBrand builder() {
        return brand -> name -> () -> new Model(brand, name);
    }

    @FunctionalInterface
    public interface ModelBuilderBrand {
        ModelBuilderName brand(Brand brand);
    }

    @FunctionalInterface
    public interface ModelBuilderName {
        ModelBuilder name(String name);
    }

    @FunctionalInterface
    public interface ModelBuilder {
        Model build();
    }

    public Brand getBrand() {
        return brand;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Model.class.getSimpleName() + "[", "]")
                .add("brand=" + brand)
                .add("name='" + name + "'")
                .add("id=" + id)
                .toString();
    }
}
