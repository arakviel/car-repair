package com.arakviel.carrepair.domain.impl;

import com.arakviel.carrepair.domain.BaseDomain;
import com.arakviel.carrepair.domain.Domain;
import java.util.StringJoiner;

public class Discount extends BaseDomain<Integer> implements Domain {

    private String name;
    private String description;
    private short value;

    private Discount(String name, short value) {
        this.name = name;
        this.value = value;
    }

    public static DiscountBuilderName builder() {
        return name -> value -> () -> new Discount(name, value);
    }

    @FunctionalInterface
    public interface DiscountBuilderName {
        DiscountBuilderValue name(String name);
    }

    @FunctionalInterface
    public interface DiscountBuilderValue {
        DiscountBuilder value(short value);
    }

    @FunctionalInterface
    public interface DiscountBuilder {
        Discount build();
    }

    public String getName() {
        return name;
    }

    public short getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Discount.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("description='" + description + "'")
                .add("value=" + value)
                .add("id=" + id)
                .toString();
    }
}
