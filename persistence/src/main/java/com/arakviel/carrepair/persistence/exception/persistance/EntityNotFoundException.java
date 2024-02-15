package com.arakviel.carrepair.persistence.exception.persistance;

/**
 * Thrown by the persistence layer when GenericDao#findOneById(Object) is called and the object no
 * longer exists in the database.
 */
public class EntityNotFoundException extends PersistenceException {
    public EntityNotFoundException() {
        super();
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
