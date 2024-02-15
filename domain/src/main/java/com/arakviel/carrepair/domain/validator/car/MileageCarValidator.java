package com.arakviel.carrepair.domain.validator.car;

import com.arakviel.carrepair.domain.impl.Car;
import com.arakviel.carrepair.domain.validator.CarValidator;
import com.arakviel.carrepair.domain.validator.util.NumberValidator;
import java.util.List;
import java.util.Map;

public class MileageCarValidator extends CarValidator {

    protected MileageCarValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages map collection.
     *
     * @param car current car to validate
     */
    @Override
    public boolean validate(Car car) {
        boolean validateResult = true;
        List<String> messages =
                NumberValidator.getInstance().getErrorMessages((int) car.getYear(), 1, Integer.MAX_VALUE, true);

        if (!messages.isEmpty()) {
            validationMessages.put("mileage", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(car);
        }

        return validateResult;
    }
}
