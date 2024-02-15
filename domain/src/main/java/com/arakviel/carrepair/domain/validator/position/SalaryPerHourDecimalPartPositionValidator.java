package com.arakviel.carrepair.domain.validator.position;

import com.arakviel.carrepair.domain.impl.Position;
import com.arakviel.carrepair.domain.validator.PositionValidator;
import com.arakviel.carrepair.domain.validator.util.NumberValidator;
import java.util.List;
import java.util.Map;

class SalaryPerHourDecimalPartPositionValidator extends PositionValidator {

    protected SalaryPerHourDecimalPartPositionValidator(Map<String, List<String>> validationMessages) {
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
        List<String> messages = NumberValidator.getInstance()
                .getErrorMessages(position.getSalaryPerHour().decimalPart(), 0, Integer.MAX_VALUE, false);

        if (!messages.isEmpty()) {
            validationMessages.put("salaryPerHourDecimalPart", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(position);
        }

        return validateResult;
    }
}
