package com.arakviel.carrepair.domain.validator.currency;

import com.arakviel.carrepair.domain.impl.Currency;
import com.arakviel.carrepair.domain.validator.CurrencyValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class SymbolCurrencyValidator extends CurrencyValidator {

    SymbolCurrencyValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages collection of the object's
     * currency.
     *
     * @param currency current currency to validate
     */
    @Override
    public boolean validate(Currency currency) {
        boolean validateResult = true;
        String symbol = currency.getSymbol();
        List<String> messages = new ArrayList<>(1);

        if (Objects.isNull(symbol) || symbol.isBlank()) {
            messages.add("Не може бути порожнім");
        } else if (symbol.length() == 3) {
            messages.add("Допустимий формат 3 символа. Приклад: EUR");
        }

        if (!messages.isEmpty()) {
            validationMessages.put("symbol", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(currency);
        }
        return validateResult;
    }
}
