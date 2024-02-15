package com.arakviel.carrepair.persistence.entity.impl;

import com.arakviel.carrepair.persistence.entity.BaseEntity;
import com.arakviel.carrepair.persistence.entity.Entity;
import java.util.StringJoiner;

public class DiscountEntity extends BaseEntity<Integer> implements Entity {

    private String name;
    private String description;
    private short value;

    private DiscountEntity(Integer id, String name, short value) {
        super(id);
        this.name = name;
        this.value = value;
    }

    public static DiscountEntityBuilderId builder() {
        return id -> name -> value -> () -> new DiscountEntity(id, name, value);
    }

    @FunctionalInterface
    public interface DiscountEntityBuilderId {

        DiscountEntityBuilderName id(Integer id);
    }

    @FunctionalInterface
    public interface DiscountEntityBuilderName {

        DiscountEntityBuilderValue name(String name);
    }

    @FunctionalInterface
    public interface DiscountEntityBuilderValue {

        DiscountEntityBuilder value(short value);
    }

    @FunctionalInterface
    public interface DiscountEntityBuilder {

        DiscountEntity build();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public short getValue() {
        return value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DiscountEntity.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("description='" + description + "'")
                .add("value=" + value)
                .add("id=" + id)
                .toString();
    }
}
