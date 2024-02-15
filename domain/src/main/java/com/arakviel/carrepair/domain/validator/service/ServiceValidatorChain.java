package com.arakviel.carrepair.domain.validator.service;

import com.arakviel.carrepair.domain.impl.Service;
import com.arakviel.carrepair.domain.validator.ServiceValidator;
import com.arakviel.carrepair.domain.validator.ValidatorChain;

public class ServiceValidatorChain extends ValidatorChain {

    ServiceValidator firstValidator;

    @Override
    protected void validateChain() {
        firstValidator = new NameServiceValidator(validationMessages);
        firstValidator
                .setNext(new DescriptionServiceValidator(validationMessages))
                .setNext(new PhotoServiceValidator(validationMessages))
                .setNext(new CurrencyServiceValidator(validationMessages))
                .setNext(new PriceWholePartServiceValidator(validationMessages))
                .setNext(new PriceDecimalPartServiceValidator(validationMessages));
    }

    public boolean validate(Service position) {
        validationMessages.clear();
        firstValidator.validate(position);
        return validationMessages.size() == 0;
    }

    private static class SingletonHolder {
        public static final ServiceValidatorChain INSTANCE = new ServiceValidatorChain();
    }

    public static ServiceValidatorChain getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
