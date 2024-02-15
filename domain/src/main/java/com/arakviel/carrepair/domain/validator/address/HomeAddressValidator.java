package com.arakviel.carrepair.domain.validator.address;

import com.arakviel.carrepair.domain.impl.Address;
import com.arakviel.carrepair.domain.validator.AddressValidator;
import com.arakviel.carrepair.domain.validator.util.InputValidator;
import java.util.List;
import java.util.Map;

class HomeAddressValidator extends AddressValidator {

    protected HomeAddressValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages map collection.
     *
     * @param address current address to validate
     */
    @Override
    public boolean validate(Address address) {
        boolean validateResult = true;
        List<String> messages = InputValidator.getInstance().getErrorMessages(address.getHome(), 1, 32, true);

        if (!messages.isEmpty()) {
            validationMessages.put("home", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(address);
        }

        return validateResult;
    }
}
