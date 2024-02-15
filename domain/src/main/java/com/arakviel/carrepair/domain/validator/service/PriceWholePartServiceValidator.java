package com.arakviel.carrepair.domain.validator.service;

import com.arakviel.carrepair.domain.impl.Service;
import com.arakviel.carrepair.domain.validator.ServiceValidator;
import com.arakviel.carrepair.domain.validator.util.NumberValidator;
import java.util.List;
import java.util.Map;

class PriceWholePartServiceValidator extends ServiceValidator {

    protected PriceWholePartServiceValidator(Map<String, List<String>> validationMessages) {
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
        List<String> messages = NumberValidator.getInstance()
                .getErrorMessages(service.getPrice().wholePart(), 0, Integer.MAX_VALUE, true);

        if (!messages.isEmpty()) {
            validationMessages.put("priceWholePart", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(service);
        }

        return validateResult;
    }
}
