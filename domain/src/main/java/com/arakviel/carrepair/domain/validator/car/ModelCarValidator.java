package com.arakviel.carrepair.domain.validator.car;

import com.arakviel.carrepair.domain.impl.Car;
import com.arakviel.carrepair.domain.validator.CarValidator;
import com.arakviel.carrepair.domain.validator.util.DomainRequireValidator;
import java.util.List;
import java.util.Map;

class ModelCarValidator extends CarValidator {

    ModelCarValidator(Map<String, List<String>> validationMessages) {
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
        List<String> messages = DomainRequireValidator.getInstance()
                .getErrorMessages(car.getModel(), car.getModel().getId());

        if (!messages.isEmpty()) {
            validationMessages.put("model", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(car);
        }

        return validateResult;
    }
}
