package com.arakviel.carrepair.domain.validator.spare;

import com.arakviel.carrepair.domain.impl.Spare;
import com.arakviel.carrepair.domain.validator.SpareValidator;
import com.arakviel.carrepair.domain.validator.util.InputValidator;
import java.util.List;
import java.util.Map;

class NameSpareValidator extends SpareValidator {

    protected NameSpareValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages map collection.
     *
     * @param spare current spare to validate
     */
    @Override
    public boolean validate(Spare spare) {
        boolean validateResult = true;
        List<String> messages = InputValidator.getInstance().getErrorMessages(spare.getName(), 2, 128, true);

        if (!messages.isEmpty()) {
            validationMessages.put("name", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(spare);
        }

        return validateResult;
    }
}
