package com.arakviel.carrepair.domain.validator.currency;

import com.arakviel.carrepair.domain.impl.Currency;
import com.arakviel.carrepair.domain.validator.CurrencyValidator;
import com.arakviel.carrepair.domain.validator.ValidatorChain;

public class CurrencyValidatorChain extends ValidatorChain {
    CurrencyValidator firstValidator;

    @Override
    protected void validateChain() {
        firstValidator = new NameCurrencyValidator(validationMessages);
        firstValidator.setNext(new SymbolCurrencyValidator(validationMessages));
    }

    public boolean validate(Currency currency) {
        validationMessages.clear();
        firstValidator.validate(currency);
        return validationMessages.size() == 0;
    }

    private static class SingletonHolder {
        public static final CurrencyValidatorChain INSTANCE = new CurrencyValidatorChain();
    }

    public static CurrencyValidatorChain getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
