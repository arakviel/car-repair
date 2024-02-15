package com.arakviel.carrepair.persistence.entity.impl;

import com.arakviel.carrepair.persistence.entity.BaseEntity;
import com.arakviel.carrepair.persistence.entity.Entity;
import java.util.StringJoiner;

public class ModelEntity extends BaseEntity<Integer> implements Entity {

    private BrandEntity brandEntity;
    private String name;

    private ModelEntity(Integer id, BrandEntity brandEntity, String name) {
        super(id);
        this.brandEntity = brandEntity;
        this.name = name;
    }

    public static ModelEntityBuilderId builder() {
        return id -> brandEntity -> name -> () -> new ModelEntity(id, brandEntity, name);
    }

    @FunctionalInterface
    public interface ModelEntityBuilderId {
        ModelEntityBuilderBrandEntity id(Integer id);
    }

    @FunctionalInterface
    public interface ModelEntityBuilderBrandEntity {
        ModelEntityBuilderName brandEntity(BrandEntity brandEntity);
    }

    @FunctionalInterface
    public interface ModelEntityBuilderName {
        ModelEntityBuilder name(String name);
    }

    @FunctionalInterface
    public interface ModelEntityBuilder {
        ModelEntity build();
    }

    public BrandEntity getBrandEntity() {
        return brandEntity;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ModelEntity.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("brandEntity=" + brandEntity)
                .add("name='" + name + "'")
                .toString();
    }
}
