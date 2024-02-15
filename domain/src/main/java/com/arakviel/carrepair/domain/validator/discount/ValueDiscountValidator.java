package com.arakviel.carrepair.domain.validator.discount;

import com.arakviel.carrepair.domain.impl.Discount;
import com.arakviel.carrepair.domain.validator.DiscountValidator;
import com.arakviel.carrepair.domain.validator.util.NumberValidator;
import java.util.List;
import java.util.Map;

class ValueDiscountValidator extends DiscountValidator {

    protected ValueDiscountValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages map collection.
     *
     * @param discount current discount to validate
     */
    @Override
    public boolean validate(Discount discount) {
        boolean validateResult = true;
        List<String> messages = NumberValidator.getInstance().getErrorMessages((int) discount.getValue(), 1, 100, true);

        if (!messages.isEmpty()) {
            validationMessages.put("value", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(discount);
        }

        return validateResult;
    }
}
