package com.arakviel.carrepair.domain.validator.phone;

import com.arakviel.carrepair.domain.impl.Phone;
import com.arakviel.carrepair.domain.validator.PhoneValidator;
import com.arakviel.carrepair.domain.validator.util.DomainRequireValidator;
import java.util.List;
import java.util.Map;

class EmployeePhoneValidator extends PhoneValidator {
    EmployeePhoneValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages collection of the object's
     * phone.
     *
     * @param phone current phone to validate
     */
    @Override
    public boolean validate(Phone phone) {
        boolean validateResult = true;
        List<String> messages = DomainRequireValidator.getInstance()
                .getErrorMessages(phone.getEmployee(), phone.getEmployee().getId());

        if (!messages.isEmpty()) {
            validationMessages.put("employee", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(phone);
        }

        return validateResult;
    }
}
