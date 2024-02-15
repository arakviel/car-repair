package com.arakviel.carrepair.persistence.impl.converter;

import java.sql.ResultSet;

public interface EntityRowConverter<E> {

    E execute(ResultSet resultSet);
}
