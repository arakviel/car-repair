package com.arakviel.carrepair.domain.validator.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InputValidator {

    public List<String> getErrorMessages(String text, int minLength, int maxLength, boolean isRequired) {
        List<String> validationMessages = new ArrayList<>();
        if (Objects.isNull(text) && isRequired) {
            validationMessages.add("Не може бути порожнім");
        } else if (Objects.nonNull(text)) {
            if (text.isBlank() && isRequired) {
                validationMessages.add("Не може бути порожнім");
            }
            if (text.trim().length() <= minLength && isRequired) {
                validationMessages.add("Повинен містити більше %d симв.".formatted(minLength));
            }
            if (text.trim().length() > maxLength) {
                validationMessages.add("Повинен містити не більше %d симв.".formatted(maxLength));
            }
        }

        return validationMessages;
    }

    private InputValidator() {}

    private static class SingletonHandler {
        public static final InputValidator INSTANCE = new InputValidator();
    }

    public static InputValidator getInstance() {
        return SingletonHandler.INSTANCE;
    }
}
