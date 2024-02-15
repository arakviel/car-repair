package com.arakviel.carrepair.persistence.entity;

import java.util.Objects;

public abstract class BaseEntity<K> {

    protected K id;

    protected BaseEntity(K id) {
        this.id = id;
    }

    public K getId() {
        return id;
    }

    public void setId(K id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseEntity<?> baseEntity = (BaseEntity<?>) o;
        return Objects.equals(id, baseEntity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
