package com.arakviel.carrepair.domain.impl;

import com.arakviel.carrepair.domain.BaseDomain;
import com.arakviel.carrepair.domain.Domain;
import java.util.StringJoiner;

public class Currency extends BaseDomain<Integer> implements Domain {

    private String name;
    private String symbol;

    private Currency(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public static CurrencyBuilderName builder() {
        return name -> symbol -> () -> new Currency(name, symbol);
    }

    @FunctionalInterface
    public interface CurrencyBuilderName {
        CurrencyBuilderSymbol name(String name);
    }

    @FunctionalInterface
    public interface CurrencyBuilderSymbol {
        CurrencyBuilder symbol(String symbol);
    }

    @FunctionalInterface
    public interface CurrencyBuilder {
        Currency build();
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Currency.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("symbol='" + symbol + "'")
                .add("id=" + id)
                .toString();
    }
}
