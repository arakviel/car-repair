package com.arakviel.carrepair.domain.validator.order;

import com.arakviel.carrepair.domain.impl.Order;
import com.arakviel.carrepair.domain.validator.OrderValidator;
import com.arakviel.carrepair.domain.validator.util.ObjectRequireValidator;
import java.util.List;
import java.util.Map;

class PaymentAtOrderValidator extends OrderValidator {

    protected PaymentAtOrderValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages map collection.
     *
     * @param order current order to validate
     */
    @Override
    public boolean validate(Order order) {
        boolean validateResult = true;
        List<String> messages = ObjectRequireValidator.getInstance().getErrorMessages(order.getPaymentAt());

        if (!messages.isEmpty()) {
            validationMessages.put("paymentAt", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(order);
        }

        return validateResult;
    }
}
