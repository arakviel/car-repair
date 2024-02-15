package com.arakviel.carrepair.domain.validator;

import com.arakviel.carrepair.domain.impl.Model;
import java.util.List;
import java.util.Map;

public abstract class ModelValidator implements Validator<Model, ModelValidator> {

    protected ModelValidator nextValidator;
    protected Map<String, List<String>> validationMessages;

    protected ModelValidator(Map<String, List<String>> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * Setting the next validation handler
     *
     * @param nextValidator next handler
     */
    @Override
    public ModelValidator setNext(ModelValidator nextValidator) {
        this.nextValidator = nextValidator;
        return this.nextValidator;
    }
}
