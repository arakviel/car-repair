package com.arakviel.carrepair.domain.repository;

import com.arakviel.carrepair.domain.impl.Brand;
import com.arakviel.carrepair.domain.impl.Model;
import java.util.List;

public interface ModelRepository extends GenericRepository<Integer, Model> {

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Entities
     */
    List<Model> getAllWhere(Brand brand, String name);
}
