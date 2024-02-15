package com.arakviel.carrepair.domain.validator.phone;

import com.arakviel.carrepair.domain.impl.Phone;
import com.arakviel.carrepair.domain.validator.PhoneValidator;
import com.arakviel.carrepair.domain.validator.util.ObjectRequireValidator;
import java.util.List;
import java.util.Map;

class PhoneTypePhoneValidator extends PhoneValidator {

    protected PhoneTypePhoneValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages map collection.
     *
     * @param phone current phone to validate
     */
    @Override
    public boolean validate(Phone phone) {
        boolean validateResult = true;
        List<String> messages = ObjectRequireValidator.getInstance().getErrorMessages(phone.getPhoneType());

        if (!messages.isEmpty()) {
            validationMessages.put("periodType", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(phone);
        }

        return validateResult;
    }
}
