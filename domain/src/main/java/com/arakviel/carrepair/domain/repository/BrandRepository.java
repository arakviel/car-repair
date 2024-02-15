package com.arakviel.carrepair.domain.repository;

import com.arakviel.carrepair.domain.impl.Brand;
import java.util.List;

public interface BrandRepository extends GenericRepository<Integer, Brand> {

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @return collection of Domains
     */
    List<Brand> getAllWhere(String name);
}
