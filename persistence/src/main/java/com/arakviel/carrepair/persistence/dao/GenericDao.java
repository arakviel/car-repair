package com.arakviel.carrepair.persistence.dao;

import com.arakviel.carrepair.persistence.entity.Entity;
import com.arakviel.carrepair.persistence.filter.FilterDto;
import java.util.List;
import java.util.Optional;

public interface GenericDao<K, E extends Entity, F extends FilterDto> {

    /**
     * Get an entity object by identifier.
     *
     * @param id primary key identifier
     * @return Optional<Entity>
     */
    Optional<E> findOneById(K id);

    /**
     * Get the entire collection of entities.
     *
     * @return collection of Entities
     */
    List<E> findAll();

    /**
     * Get the entire collection of entities by filtering in RDMS.
     *
     * @param entityDtoFiler values for where conditions
     * @return collection of Entities
     */
    List<E> findAll(F entityDtoFiler);

    /**
     * Save or update the entity to the database table.
     *
     * @param entity persistent entity
     * @return entity, with identifier of the last added record from the database
     */
    E save(E entity);

    /**
     * Delete an entity from the database table by identifier.
     *
     * @param id primary key identifier
     */
    void remove(K id);
}
