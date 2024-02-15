package com.arakviel.carrepair.domain.validator.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhotoRequireValidator {

    public List<String> getErrorMessages(byte[] bytes) {
        List<String> validationMessages = new ArrayList<>();
        if (Objects.isNull(bytes)) {
            validationMessages.add("Не може бути порожнім");
        } else if (bytes.length == 0) {
            validationMessages.add("Не може бути порожнім");
        }
        return validationMessages;
    }

    private PhotoRequireValidator() {}

    private static class SingletonHandler {
        public static final PhotoRequireValidator INSTANCE = new PhotoRequireValidator();
    }

    public static PhotoRequireValidator getInstance() {
        return SingletonHandler.INSTANCE;
    }
}
