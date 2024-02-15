package com.arakviel.carrepair.domain.validator;

import com.arakviel.carrepair.domain.impl.Service;
import java.util.List;
import java.util.Map;

public abstract class ServiceValidator implements Validator<Service, ServiceValidator> {

    protected ServiceValidator nextValidator;
    protected Map<String, List<String>> validationMessages;

    protected ServiceValidator(Map<String, List<String>> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * Setting the next validation handler
     *
     * @param nextValidator next handler
     */
    @Override
    public ServiceValidator setNext(ServiceValidator nextValidator) {
        this.nextValidator = nextValidator;
        return this.nextValidator;
    }
}
