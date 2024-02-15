package com.arakviel.carrepair.domain.repository;

import com.arakviel.carrepair.domain.Domain;
import java.util.List;
import java.util.Map;

public interface GenericRepository<K, D extends Domain> {

    /**
     * Get a domain object by identifier.
     *
     * @param id primary key identifier
     * @return D domain object
     */
    D get(K id);

    /**
     * Get the entire collection of entities.
     *
     * @return collection of Entities
     */
    List<D> getAll();

    /**
     * Save the entity to the database table.
     *
     * @param domain persistent entity
     * @return entity, with identifier of the last added record from the database
     */
    D add(D domain);

    /**
     * Update the domain to the database table.
     *
     * @param id identifier
     * @param domain persistent entity
     */
    void set(K id, D domain);

    /**
     * Delete domain object from collection.
     *
     * @param id primary key identifier
     */
    void remove(K id);

    Map<String, List<String>> getValidationMessages();
}
