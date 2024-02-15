package com.arakviel.carrepair.persistence.entity.impl;

import com.arakviel.carrepair.persistence.entity.BaseEntity;
import com.arakviel.carrepair.persistence.entity.Entity;
import java.util.StringJoiner;

public class BrandEntity extends BaseEntity<Integer> implements Entity {
    private String name;

    private BrandEntity(Integer id, String name) {
        super(id);
        this.name = name;
    }

    public static BrandEntityBuilderId builder() {
        return id -> name -> () -> new BrandEntity(id, name);
    }

    @FunctionalInterface
    public interface BrandEntityBuilderId {
        BrandEntityBuilderName id(Integer id);
    }

    @FunctionalInterface
    public interface BrandEntityBuilderName {
        BrandEntityBuilder name(String name);
    }

    @FunctionalInterface
    public interface BrandEntityBuilder {
        BrandEntity build();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BrandEntity.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .toString();
    }
}
