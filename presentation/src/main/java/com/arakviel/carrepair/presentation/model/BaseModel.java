package com.arakviel.carrepair.presentation.model;

import java.util.Objects;
import javafx.beans.property.ObjectProperty;

public abstract class BaseModel<K> {

    protected ObjectProperty<K> id;

    public K getId() {
        return id.get();
    }

    public ObjectProperty<K> idProperty() {
        return id;
    }

    public void setId(K id) {
        this.id.set(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseModel<?> baseModel = (BaseModel<?>) o;
        return Objects.equals(id, baseModel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
