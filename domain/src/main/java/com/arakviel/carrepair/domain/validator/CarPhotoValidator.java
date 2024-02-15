package com.arakviel.carrepair.domain.validator;

import com.arakviel.carrepair.domain.impl.CarPhoto;
import java.util.List;
import java.util.Map;

public abstract class CarPhotoValidator implements Validator<CarPhoto, CarPhotoValidator> {

    protected CarPhotoValidator nextValidator;
    protected Map<String, List<String>> validationMessages;

    protected CarPhotoValidator(Map<String, List<String>> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * Setting the next validation handler
     *
     * @param nextValidator next handler
     */
    @Override
    public CarPhotoValidator setNext(CarPhotoValidator nextValidator) {
        this.nextValidator = nextValidator;
        return this.nextValidator;
    }
}
