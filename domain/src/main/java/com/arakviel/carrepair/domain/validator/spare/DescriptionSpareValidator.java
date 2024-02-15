package com.arakviel.carrepair.domain.validator.spare;

import com.arakviel.carrepair.domain.impl.Spare;
import com.arakviel.carrepair.domain.validator.SpareValidator;
import com.arakviel.carrepair.domain.validator.util.InputValidator;
import java.util.List;
import java.util.Map;

class DescriptionSpareValidator extends SpareValidator {

    protected DescriptionSpareValidator(Map<String, List<String>> validationMessages) {
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
        List<String> messages = InputValidator.getInstance().getErrorMessages(spare.getDescription(), 0, 512, false);

        if (!messages.isEmpty()) {
            validationMessages.put("description", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(spare);
        }

        return validateResult;
    }
}
