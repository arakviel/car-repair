package com.arakviel.carrepair.domain.validator.util;

import com.arakviel.carrepair.domain.Domain;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DomainRequireValidator {

    public <D extends Domain, K> List<String> getErrorMessages(D domain, K id) {
        List<String> validationMessages = new ArrayList<>();
        if (Objects.isNull(domain)) {
            validationMessages.add("Не може бути порожнім");
        }
        if (Objects.isNull(id)) {
            validationMessages.add("Ідентифікатор не може бути порожнім");
        }

        return validationMessages;
    }

    private DomainRequireValidator() {}

    private static class SingletonHandler {
        public static final DomainRequireValidator INSTANCE = new DomainRequireValidator();
    }

    public static DomainRequireValidator getInstance() {
        return SingletonHandler.INSTANCE;
    }
}
