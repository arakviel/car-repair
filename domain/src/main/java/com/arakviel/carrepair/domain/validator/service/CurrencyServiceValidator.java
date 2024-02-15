package com.arakviel.carrepair.domain.validator.service;

import com.arakviel.carrepair.domain.impl.Service;
import com.arakviel.carrepair.domain.validator.ServiceValidator;
import com.arakviel.carrepair.domain.validator.util.DomainRequireValidator;
import java.util.List;
import java.util.Map;

class CurrencyServiceValidator extends ServiceValidator {

    protected CurrencyServiceValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages map collection.
     *
     * @param service current service to validate
     */
    @Override
    public boolean validate(Service service) {
        boolean validateResult = true;
        List<String> messages = DomainRequireValidator.getInstance()
                .getErrorMessages(service.getCurrency(), service.getCurrency().getId());

        if (!messages.isEmpty()) {
            validationMessages.put("currency", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(service);
        }

        return validateResult;
    }
}
