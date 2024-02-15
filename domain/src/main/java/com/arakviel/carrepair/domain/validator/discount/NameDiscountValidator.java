package com.arakviel.carrepair.domain.validator.discount;

import com.arakviel.carrepair.domain.impl.Discount;
import com.arakviel.carrepair.domain.validator.DiscountValidator;
import com.arakviel.carrepair.domain.validator.util.InputValidator;
import java.util.List;
import java.util.Map;

class NameDiscountValidator extends DiscountValidator {

    protected NameDiscountValidator(Map<String, List<String>> validationMessages) {
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
        List<String> messages = InputValidator.getInstance().getErrorMessages(discount.getName(), 2, 64, true);

        if (!messages.isEmpty()) {
            validationMessages.put("name", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(discount);
        }

        return validateResult;
    }
}
