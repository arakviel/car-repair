package com.arakviel.carrepair.domain.validator.car;

import com.arakviel.carrepair.domain.impl.Car;
import com.arakviel.carrepair.domain.validator.CarValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ColorCarValidator extends CarValidator {

    ColorCarValidator(Map<String, List<String>> validationMessages) {
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
        String color = car.toHexColorString();
        List<String> messages = new ArrayList<>(1);

        if (Objects.nonNull(color) && !color.matches("^[0-9A-Fa-f]{6}$")) {
            messages.add("Лише HEX формат з 6 символів");
        }

        if (!messages.isEmpty()) {
            validationMessages.put("color", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(car);
        }
        return validateResult;
    }
}
