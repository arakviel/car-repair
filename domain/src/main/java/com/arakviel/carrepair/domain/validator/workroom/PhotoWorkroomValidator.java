package com.arakviel.carrepair.domain.validator.workroom;

import com.arakviel.carrepair.domain.impl.Workroom;
import com.arakviel.carrepair.domain.validator.WorkroomValidator;
import com.arakviel.carrepair.domain.validator.util.PhotoRequireValidator;
import java.util.List;
import java.util.Map;

public class PhotoWorkroomValidator extends WorkroomValidator {

    protected PhotoWorkroomValidator(Map<String, List<String>> validationMessages) {
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
        List<String> messages = PhotoRequireValidator.getInstance().getErrorMessages(workroom.getPhoto());

        if (!messages.isEmpty()) {
            validationMessages.put("photo", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(workroom);
        }

        return validateResult;
    }
}
