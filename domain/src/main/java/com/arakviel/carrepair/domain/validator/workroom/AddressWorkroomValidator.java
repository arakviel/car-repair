package com.arakviel.carrepair.domain.validator.workroom;

import com.arakviel.carrepair.domain.impl.Workroom;
import com.arakviel.carrepair.domain.validator.WorkroomValidator;
import com.arakviel.carrepair.domain.validator.util.DomainRequireValidator;
import java.util.List;
import java.util.Map;

class AddressWorkroomValidator extends WorkroomValidator {

    protected AddressWorkroomValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages map collection.
     *
     * @param workroom current workroom to validate
     */
    @Override
    public boolean validate(Workroom workroom) {
        boolean validateResult = true;
        List<String> messages = DomainRequireValidator.getInstance()
                .getErrorMessages(workroom.getAddress(), workroom.getAddress().getId());

        if (!messages.isEmpty()) {
            validationMessages.put("address", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(workroom);
        }

        return validateResult;
    }
}
