package com.arakviel.carrepair.domain.validator.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NumberValidator {

    public List<String> getErrorMessages(Integer number, int min, int max, boolean isRequired) {
        List<String> validationMessages = new ArrayList<>();
        if (Objects.isNull(number) && isRequired) {
            validationMessages.add("Не може бути порожнім");
        } else if (Objects.nonNull(number)) {
            if (number <= min && isRequired) {
                validationMessages.add("Значення повинно бути більше %d".formatted(min));
            }
            if (number > max) {
                validationMessages.add("Значення повинно бути не більше %d".formatted(max));
            }
        }

        return validationMessages;
    }

    private NumberValidator() {}

    private static class SingletonHandler {
        public static final NumberValidator INSTANCE = new NumberValidator();
    }

    public static NumberValidator getInstance() {
        return SingletonHandler.INSTANCE;
    }
}
