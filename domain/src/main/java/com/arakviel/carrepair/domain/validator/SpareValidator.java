package com.arakviel.carrepair.domain.validator;

import com.arakviel.carrepair.domain.impl.Spare;
import java.util.List;
import java.util.Map;

public abstract class SpareValidator implements Validator<Spare, SpareValidator> {

    protected SpareValidator nextValidator;
    protected Map<String, List<String>> validationMessages;

    protected SpareValidator(Map<String, List<String>> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * Setting the next validation handler
     *
     * @param nextValidator next handler
     */
    @Override
    public SpareValidator setNext(SpareValidator nextValidator) {
        this.nextValidator = nextValidator;
        return this.nextValidator;
    }
}
