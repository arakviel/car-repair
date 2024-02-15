package com.arakviel.carrepair.domain.validator;

import com.arakviel.carrepair.domain.impl.Currency;
import java.util.List;
import java.util.Map;

public abstract class CurrencyValidator implements Validator<Currency, CurrencyValidator> {

    protected CurrencyValidator nextValidator;
    protected Map<String, List<String>> validationMessages;

    protected CurrencyValidator(Map<String, List<String>> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * Setting the next validation handler
     *
     * @param nextValidator next handler
     */
    @Override
    public CurrencyValidator setNext(CurrencyValidator nextValidator) {
        this.nextValidator = nextValidator;
        return this.nextValidator;
    }
}
