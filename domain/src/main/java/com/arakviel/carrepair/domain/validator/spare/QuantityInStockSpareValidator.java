package com.arakviel.carrepair.domain.validator.spare;

import com.arakviel.carrepair.domain.impl.Spare;
import com.arakviel.carrepair.domain.validator.SpareValidator;
import com.arakviel.carrepair.domain.validator.util.NumberValidator;
import java.util.List;
import java.util.Map;

class QuantityInStockSpareValidator extends SpareValidator {

    protected QuantityInStockSpareValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages map collection.
     *
     * @param service current service to validate
     */
    @Override
    public boolean validate(Spare service) {
        boolean validateResult = true;
        List<String> messages = NumberValidator.getInstance()
                .getErrorMessages(service.getPrice().wholePart(), 0, Integer.MAX_VALUE, true);

        if (!messages.isEmpty()) {
            validationMessages.put("quantity", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(service);
        }

        return validateResult;
    }
}
