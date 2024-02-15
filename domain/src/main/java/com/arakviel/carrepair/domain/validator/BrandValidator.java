package com.arakviel.carrepair.domain.validator;

import com.arakviel.carrepair.domain.impl.Brand;
import java.util.List;
import java.util.Map;

public abstract class BrandValidator implements Validator<Brand, BrandValidator> {

    protected BrandValidator nextValidator;
    protected Map<String, List<String>> validationMessages;

    protected BrandValidator(Map<String, List<String>> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * Setting the next validation handler
     *
     * @param nextValidator next handler
     */
    @Override
    public BrandValidator setNext(BrandValidator nextValidator) {
        this.nextValidator = nextValidator;
        return this.nextValidator;
    }
}
