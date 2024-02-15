package com.arakviel.carrepair.presentation.model.impl;

import com.arakviel.carrepair.presentation.model.BaseModel;
import com.arakviel.carrepair.presentation.model.Model;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CurrencyModel extends BaseModel<Integer> implements Model, Cloneable {

    private StringProperty name = new SimpleStringProperty();
    private StringProperty symbol = new SimpleStringProperty();

    private CurrencyModel(String name, String symbol) {
        this.id = new SimpleObjectProperty<>();
        this.name.set(name);
        this.symbol.set(symbol);
    }

    public static CurrencyModelBuilderName builder() {
        return name -> symbol -> () -> new CurrencyModel(name, symbol);
    }

    @FunctionalInterface
    public interface CurrencyModelBuilderName {
        CurrencyModelBuilderSymbol name(String name);
    }

    @FunctionalInterface
    public interface CurrencyModelBuilderSymbol {
        CurrencyModelBuilder symbol(String symbol);
    }

    @FunctionalInterface
    public interface CurrencyModelBuilder {
        CurrencyModel build();
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getSymbol() {
        return symbol.get();
    }

    public StringProperty symbolProperty() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol.set(symbol);
    }

    @Override
    public CurrencyModel clone() {
        try {
            CurrencyModel cloned = (CurrencyModel) super.clone();
            cloned.id = new SimpleObjectProperty<>(this.id.get());
            cloned.name = new SimpleStringProperty(this.name.get());
            cloned.symbol = new SimpleStringProperty(this.symbol.get());

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public String toString() {
        return "%s (%s)".formatted(name.get(), symbol.get());
    }
}
