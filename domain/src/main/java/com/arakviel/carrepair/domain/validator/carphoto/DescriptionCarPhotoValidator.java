package com.arakviel.carrepair.domain.validator.carphoto;

import com.arakviel.carrepair.domain.impl.CarPhoto;
import com.arakviel.carrepair.domain.validator.CarPhotoValidator;
import com.arakviel.carrepair.domain.validator.util.InputValidator;
import java.util.List;
import java.util.Map;

class DescriptionCarPhotoValidator extends CarPhotoValidator {

    protected DescriptionCarPhotoValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages map collection.
     *
     * @param carPhoto current carPhoto to validate
     */
    @Override
    public boolean validate(CarPhoto carPhoto) {
        boolean validateResult = true;
        List<String> messages = InputValidator.getInstance().getErrorMessages(carPhoto.getDescription(), 0, 512, false);

        if (!messages.isEmpty()) {
            validationMessages.put("description", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(carPhoto);
        }

        return validateResult;
    }
}
