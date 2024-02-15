package com.arakviel.carrepair.presentation.model.impl;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class MoneyModel implements Cloneable {

    private final IntegerProperty wholePart = new SimpleIntegerProperty();
    private final IntegerProperty decimalPart = new SimpleIntegerProperty();

    public MoneyModel(int wholePart, int decimalPart) {
        this.wholePart.set(wholePart);
        this.decimalPart.set(decimalPart);
    }

    public int wholePart() {
        return wholePart.get();
    }

    public IntegerProperty wholePartProperty() {
        return wholePart;
    }

    public void setWholePart(int wholePart) {
        this.wholePart.set(wholePart);
    }

    public int decimalPart() {
        return decimalPart.get();
    }

    public IntegerProperty decimalPartProperty() {
        return decimalPart;
    }

    public void setDecimalPart(int decimalPart) {
        this.decimalPart.set(decimalPart);
    }

    @Override
    public MoneyModel clone() {
        return new MoneyModel(wholePart(), decimalPart());
    }

    @Override
    public String toString() {
        return "%d.%d".formatted(wholePart.get(), decimalPart.get());
    }
}
