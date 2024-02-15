package com.arakviel.carrepair.domain.validator.order;

import com.arakviel.carrepair.domain.impl.Order;
import com.arakviel.carrepair.domain.validator.OrderValidator;
import com.arakviel.carrepair.domain.validator.util.DomainRequireValidator;
import java.util.List;
import java.util.Map;

class DiscountOrderValidator extends OrderValidator {

    DiscountOrderValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages collection of the object's
     * order.
     *
     * @param order current order to validate
     */
    @Override
    public boolean validate(Order order) {
        boolean validateResult = true;
        List<String> messages = DomainRequireValidator.getInstance()
                .getErrorMessages(order.getDiscount(), order.getDiscount().getId());

        if (!messages.isEmpty()) {
            validationMessages.put("discount", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(order);
        }

        return validateResult;
    }
}
