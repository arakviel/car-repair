package com.arakviel.carrepair.domain.validator;

import com.arakviel.carrepair.domain.Domain;

public interface Validator<D extends Domain, V> {

    /**
     * A validation process that writes errors to the validationMessages map collection.
     *
     * @param domain current domain to validate
     */
    boolean validate(D domain);

    /**
     * Setting the next validation handler
     *
     * @param nextValidator next handler
     */
    V setNext(V nextValidator);
}
