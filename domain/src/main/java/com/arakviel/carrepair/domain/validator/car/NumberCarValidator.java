package com.arakviel.carrepair.domain.validator.car;

import com.arakviel.carrepair.domain.impl.Car;
import com.arakviel.carrepair.domain.validator.CarValidator;
import com.arakviel.carrepair.domain.validator.util.InputValidator;
import java.util.List;
import java.util.Map;

class NumberCarValidator extends CarValidator {

    NumberCarValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages collection of the object's
     * car.
     *
     * @param car current car to validate
     */
    @Override
    public boolean validate(Car car) {
        boolean validateResult = true;
        List<String> messages = InputValidator.getInstance().getErrorMessages(car.getNumber(), 7, 12, true);

        if (!messages.isEmpty()) {
            validationMessages.put("number", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(car);
        }
        return validateResult;
    }
}
