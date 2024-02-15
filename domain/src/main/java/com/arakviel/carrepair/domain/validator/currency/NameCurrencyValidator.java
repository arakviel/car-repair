package com.arakviel.carrepair.domain.validator.currency;

import com.arakviel.carrepair.domain.impl.Currency;
import com.arakviel.carrepair.domain.validator.CurrencyValidator;
import com.arakviel.carrepair.domain.validator.util.InputValidator;
import java.util.List;
import java.util.Map;

class NameCurrencyValidator extends CurrencyValidator {

    protected NameCurrencyValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages map collection.
     *
     * @param currency current currency to validate
     */
    @Override
    public boolean validate(Currency currency) {
        boolean validateResult = true;
        List<String> messages = InputValidator.getInstance().getErrorMessages(currency.getName(), 4, 32, true);

        if (!messages.isEmpty()) {
            validationMessages.put("name", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(currency);
        }

        return validateResult;
    }
}
