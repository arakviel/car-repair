package com.arakviel.carrepair.domain.validator.order;

import com.arakviel.carrepair.domain.impl.Order;
import com.arakviel.carrepair.domain.validator.OrderValidator;
import com.arakviel.carrepair.domain.validator.util.NumberValidator;
import java.util.List;
import java.util.Map;

class PriceWholePartOrderValidator extends OrderValidator {

    protected PriceWholePartOrderValidator(Map<String, List<String>> validationMessages) {
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
        List<String> messages = NumberValidator.getInstance()
                .getErrorMessages(order.getPrice().wholePart(), 0, Integer.MAX_VALUE, true);

        if (!messages.isEmpty()) {
            validationMessages.put("priceWholePart", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(order);
        }

        return validateResult;
    }
}
