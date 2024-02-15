package com.arakviel.carrepair.domain.validator.service;

import com.arakviel.carrepair.domain.impl.Service;
import com.arakviel.carrepair.domain.validator.ServiceValidator;
import com.arakviel.carrepair.domain.validator.util.InputValidator;
import java.util.List;
import java.util.Map;

class NameServiceValidator extends ServiceValidator {

    protected NameServiceValidator(Map<String, List<String>> validationMessages) {
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
        List<String> messages = InputValidator.getInstance().getErrorMessages(service.getName(), 2, 128, true);

        if (!messages.isEmpty()) {
            validationMessages.put("name", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(service);
        }

        return validateResult;
    }
}
