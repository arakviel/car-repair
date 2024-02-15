package com.arakviel.carrepair.domain.validator.position;

import com.arakviel.carrepair.domain.impl.Position;
import com.arakviel.carrepair.domain.validator.PositionValidator;
import com.arakviel.carrepair.domain.validator.util.InputValidator;
import java.util.List;
import java.util.Map;

class NamePositionValidator extends PositionValidator {

    protected NamePositionValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages map collection.
     *
     * @param position current position to validate
     */
    @Override
    public boolean validate(Position position) {
        boolean validateResult = true;
        List<String> messages = InputValidator.getInstance().getErrorMessages(position.getName(), 2, 64, true);

        if (!messages.isEmpty()) {
            validationMessages.put("name", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(position);
        }

        return validateResult;
    }
}
