package com.arakviel.carrepair.domain.validator.discount;

import com.arakviel.carrepair.domain.impl.Discount;
import com.arakviel.carrepair.domain.validator.DiscountValidator;
import com.arakviel.carrepair.domain.validator.util.InputValidator;
import java.util.List;
import java.util.Map;

class DescriptionDiscountValidator extends DiscountValidator {

    protected DescriptionDiscountValidator(Map<String, List<String>> validationMessages) {
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
        List<String> messages = InputValidator.getInstance().getErrorMessages(discount.getDescription(), 0, 256, false);

        if (!messages.isEmpty()) {
            validationMessages.put("description", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(discount);
        }

        return validateResult;
    }
}
