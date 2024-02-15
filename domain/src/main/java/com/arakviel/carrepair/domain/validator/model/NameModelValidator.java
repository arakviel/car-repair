package com.arakviel.carrepair.domain.validator.model;

import com.arakviel.carrepair.domain.impl.Model;
import com.arakviel.carrepair.domain.validator.ModelValidator;
import com.arakviel.carrepair.domain.validator.util.InputValidator;
import java.util.List;
import java.util.Map;

class NameModelValidator extends ModelValidator {

    protected NameModelValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages map collection.
     *
     * @param model current model to validate
     */
    @Override
    public boolean validate(Model model) {
        boolean validateResult = true;
        List<String> messages = InputValidator.getInstance().getErrorMessages(model.getName(), 1, 256, true);

        if (!messages.isEmpty()) {
            validationMessages.put("name", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(model);
        }

        return validateResult;
    }
}
