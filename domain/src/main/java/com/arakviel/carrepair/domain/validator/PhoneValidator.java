package com.arakviel.carrepair.domain.validator;

import com.arakviel.carrepair.domain.impl.Phone;
import java.util.List;
import java.util.Map;

public abstract class PhoneValidator implements Validator<Phone, PhoneValidator> {

    protected PhoneValidator nextValidator;
    protected Map<String, List<String>> validationMessages;

    protected PhoneValidator(Map<String, List<String>> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * Setting the next validation handler
     *
     * @param nextValidator next handler
     */
    @Override
    public PhoneValidator setNext(PhoneValidator nextValidator) {
        this.nextValidator = nextValidator;
        return this.nextValidator;
    }
}
