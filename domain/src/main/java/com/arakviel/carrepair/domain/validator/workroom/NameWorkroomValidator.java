package com.arakviel.carrepair.domain.validator.workroom;

import com.arakviel.carrepair.domain.impl.Workroom;
import com.arakviel.carrepair.domain.validator.WorkroomValidator;
import com.arakviel.carrepair.domain.validator.util.InputValidator;
import java.util.List;
import java.util.Map;

public class NameWorkroomValidator extends WorkroomValidator {

    protected NameWorkroomValidator(Map<String, List<String>> validationMessages) {
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
        List<String> messages = InputValidator.getInstance().getErrorMessages(workroom.getName(), 2, 64, true);

        if (!messages.isEmpty()) {
            validationMessages.put("name", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(workroom);
        }

        return validateResult;
    }
}
