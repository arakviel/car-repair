package com.arakviel.carrepair.domain.validator.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhoneValueValidator {

    public List<String> getErrorMessages(String phone, boolean isRequired) {
        List<String> validationMessages = new ArrayList<>();
        if (Objects.isNull(phone) && isRequired) {
            validationMessages.add("Не може бути порожнім");
        } else if (Objects.nonNull(phone)) {
            if (phone.isBlank() && isRequired) {
                validationMessages.add("Не може бути порожнім");
            }
            if (!phone.matches("\\d+")) {
                validationMessages.add("Лише цифри");
            }
            if (phone.trim().length() <= 7) {
                validationMessages.add("Повинен містити більше 6 цифр");
            }
            if (phone.trim().length() > 16) {
                validationMessages.add("Повинен містити не більше 15 цифр");
            }
        }

        return validationMessages;
    }

    private PhoneValueValidator() {}

    private static class SingletonHandler {
        public static final PhoneValueValidator INSTANCE = new PhoneValueValidator();
    }

    public static PhoneValueValidator getInstance() {
        return SingletonHandler.INSTANCE;
    }
}
