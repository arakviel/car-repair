package com.arakviel.carrepair.domain.validator.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NameValidator {

    public List<String> getErrorMessages(String name, boolean isRequired, boolean isUa) {
        List<String> validationMessages = new ArrayList<>();
        if (Objects.isNull(name) && isRequired) {
            validationMessages.add("Не може бути порожнім");
        } else if (Objects.nonNull(name)) {
            if (name.isBlank() && isRequired) {
                validationMessages.add("Не може бути порожнім");
            }
            if (name.trim().length() < 2 && isRequired) {
                validationMessages.add("Повинен містити більше 1 символа");
            }
            if (!name.matches("[А-ЩЬЮЯҐЄІЇа-щьюяґєії']+") && isUa) {
                validationMessages.add("Лише українські символи та символ апострофу");
            }
            if (name.trim().length() > 256) {
                validationMessages.add("Повинен містити не більше 256 символів");
            }
        }

        return validationMessages;
    }

    private NameValidator() {}

    private static class SingletonHandler {
        public static final NameValidator INSTANCE = new NameValidator();
    }

    public static NameValidator getInstance() {
        return SingletonHandler.INSTANCE;
    }
}
