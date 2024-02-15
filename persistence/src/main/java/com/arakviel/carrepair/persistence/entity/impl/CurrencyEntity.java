package com.arakviel.carrepair.persistence.entity.impl;

import com.arakviel.carrepair.persistence.entity.BaseEntity;
import com.arakviel.carrepair.persistence.entity.Entity;
import java.util.StringJoiner;

public class CurrencyEntity extends BaseEntity<Integer> implements Entity {

    private String name;
    private String symbol;

    private CurrencyEntity(Integer id, String name, String symbol) {
        super(id);
        this.name = name;
        this.symbol = symbol;
    }

    public static CurrencyEntityBuilderId builder() {
        return id -> name -> symbol -> () -> new CurrencyEntity(id, name, symbol);
    }

    @FunctionalInterface
    public interface CurrencyEntityBuilderId {
        CurrencyEntityBuilderName id(Integer id);
    }

    @FunctionalInterface
    public interface CurrencyEntityBuilderName {
        CurrencyEntityBuilderSymbol name(String name);
    }

    @FunctionalInterface
    public interface CurrencyEntityBuilderSymbol {
        CurrencyEntityBuilder symbol(String symbol);
    }

    @FunctionalInterface
    public interface CurrencyEntityBuilder {
        CurrencyEntity build();
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CurrencyEntity.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("currencySymbol=" + symbol)
                .add("id=" + id)
                .toString();
    }
}
