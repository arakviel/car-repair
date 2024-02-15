package com.arakviel.carrepair.domain.validator.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ObjectRequireValidator {

    public List<String> getErrorMessages(Object object) {
        List<String> validationMessages = new ArrayList<>();
        if (Objects.isNull(object)) {
            validationMessages.add("Не може бути порожнім");
        }
        return validationMessages;
    }

    private ObjectRequireValidator() {}

    private static class SingletonHandler {
        public static final ObjectRequireValidator INSTANCE = new ObjectRequireValidator();
    }

    public static ObjectRequireValidator getInstance() {
        return SingletonHandler.INSTANCE;
    }
}
