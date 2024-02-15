package com.arakviel.carrepair.domain.validator;

import com.arakviel.carrepair.domain.impl.Discount;
import java.util.List;
import java.util.Map;

public abstract class DiscountValidator implements Validator<Discount, DiscountValidator> {

    protected DiscountValidator nextValidator;
    protected Map<String, List<String>> validationMessages;

    protected DiscountValidator(Map<String, List<String>> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * Setting the next validation handler
     *
     * @param nextValidator next handler
     */
    @Override
    public DiscountValidator setNext(DiscountValidator nextValidator) {
        this.nextValidator = nextValidator;
        return this.nextValidator;
    }
}
