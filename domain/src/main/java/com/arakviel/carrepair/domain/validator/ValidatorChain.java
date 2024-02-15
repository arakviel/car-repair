package com.arakviel.carrepair.domain.validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ValidatorChain {
    protected final Map<String, List<String>> validationMessages;

    protected ValidatorChain() {
        this.validationMessages = new HashMap<>();
        validateChain();
    }

    protected abstract void validateChain();

    public Map<String, List<String>> getValidationMessages() {
        return validationMessages;
    }
}
