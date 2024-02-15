package com.arakviel.carrepair.domain.validator.client;

import com.arakviel.carrepair.domain.impl.Client;
import com.arakviel.carrepair.domain.validator.ClientValidator;
import com.arakviel.carrepair.domain.validator.util.NameValidator;
import java.util.List;
import java.util.Map;

class MiddleNameClientValidator extends ClientValidator {

    MiddleNameClientValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages collection of the object's
     * client.
     *
     * @param client current client to validate
     */
    @Override
    public boolean validate(Client client) {
        boolean validateResult = true;
        List<String> errorMessages = NameValidator.getInstance().getErrorMessages(client.getFirstName(), false, true);

        if (!errorMessages.isEmpty()) {
            validationMessages.put("middleName", errorMessages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(client);
        }

        return validateResult;
    }
}
