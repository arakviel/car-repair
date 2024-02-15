package com.arakviel.carrepair.domain.validator.client;

import com.arakviel.carrepair.domain.impl.Client;
import com.arakviel.carrepair.domain.validator.ClientValidator;
import com.arakviel.carrepair.domain.validator.util.PhoneValueValidator;
import java.util.List;
import java.util.Map;

class PhoneClientValidator extends ClientValidator {

    PhoneClientValidator(Map<String, List<String>> validationMessages) {
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
        List<String> messages = PhoneValueValidator.getInstance().getErrorMessages(client.getPhone(), true);

        if (!messages.isEmpty()) {
            validationMessages.put("phone", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(client);
        }
        return validateResult;
    }
}
