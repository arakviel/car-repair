package com.arakviel.carrepair.domain.impl;

import com.arakviel.carrepair.domain.BaseDomain;
import com.arakviel.carrepair.domain.Domain;
import java.util.StringJoiner;

public class Brand extends BaseDomain<Integer> implements Domain {

    private String name;

    private Brand(String name) {
        this.name = name;
    }

    public static BrandBuilderName builder() {
        return name -> () -> new Brand(name);
    }

    @FunctionalInterface
    public interface BrandBuilderName {
        BrandBuilder name(String name);
    }

    @FunctionalInterface
    public interface BrandBuilder {
        Brand build();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Brand.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("id=" + id)
                .toString();
    }
}
