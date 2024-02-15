package com.arakviel.carrepair.domain.validator.phone;

import com.arakviel.carrepair.domain.impl.Phone;
import com.arakviel.carrepair.domain.validator.PhoneValidator;
import com.arakviel.carrepair.domain.validator.util.PhoneValueValidator;
import java.util.List;
import java.util.Map;

class ValuePhoneValidator extends PhoneValidator {

    ValuePhoneValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages collection of the object's
     * client.
     *
     * @param client current client to validate
     */
    @Override
    public boolean validate(Phone client) {
        boolean validateResult = true;
        List<String> messages = PhoneValueValidator.getInstance().getErrorMessages(client.getValue(), true);

        if (!messages.isEmpty()) {
            validationMessages.put("value", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(client);
        }
        return validateResult;
    }
}
