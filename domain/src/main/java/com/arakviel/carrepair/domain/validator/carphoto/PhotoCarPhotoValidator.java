package com.arakviel.carrepair.domain.validator.carphoto;

import com.arakviel.carrepair.domain.impl.CarPhoto;
import com.arakviel.carrepair.domain.validator.CarPhotoValidator;
import com.arakviel.carrepair.domain.validator.util.PhotoRequireValidator;
import java.util.List;
import java.util.Map;

class PhotoCarPhotoValidator extends CarPhotoValidator {

    protected PhotoCarPhotoValidator(Map<String, List<String>> validationMessages) {
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
        List<String> messages = PhotoRequireValidator.getInstance().getErrorMessages(carPhoto.getPhoto());

        if (!messages.isEmpty()) {
            validationMessages.put("photo", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(carPhoto);
        }

        return validateResult;
    }
}
