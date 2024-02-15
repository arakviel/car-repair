package com.arakviel.carrepair.domain.validator.position;

import com.arakviel.carrepair.domain.impl.Position;
import com.arakviel.carrepair.domain.validator.PositionValidator;
import com.arakviel.carrepair.domain.validator.util.InputValidator;
import java.util.List;
import java.util.Map;

class DescriptionPositionValidator extends PositionValidator {

    protected DescriptionPositionValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages map collection.
     *
     * @param discount current discount to validate
     */
    @Override
    public boolean validate(Position discount) {
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
