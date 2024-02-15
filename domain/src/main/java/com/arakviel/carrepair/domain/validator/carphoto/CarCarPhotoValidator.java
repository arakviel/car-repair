package com.arakviel.carrepair.domain.validator.carphoto;

import com.arakviel.carrepair.domain.impl.CarPhoto;
import com.arakviel.carrepair.domain.validator.CarPhotoValidator;
import com.arakviel.carrepair.domain.validator.util.DomainRequireValidator;
import java.util.List;
import java.util.Map;

class CarCarPhotoValidator extends CarPhotoValidator {

    CarCarPhotoValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages collection of the object's
     * carPhoto.
     *
     * @param carPhoto current carPhoto to validate
     */
    @Override
    public boolean validate(CarPhoto carPhoto) {
        boolean validateResult = true;
        List<String> messages = DomainRequireValidator.getInstance()
                .getErrorMessages(carPhoto.getCar(), carPhoto.getCar().getId());

        if (!messages.isEmpty()) {
            validationMessages.put("car", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(carPhoto);
        }

        return validateResult;
    }
}
