package com.arakviel.carrepair.domain.validator;

import com.arakviel.carrepair.domain.impl.Car;
import java.util.List;
import java.util.Map;

public abstract class CarValidator implements Validator<Car, CarValidator> {

    protected CarValidator nextValidator;
    protected Map<String, List<String>> validationMessages;

    protected CarValidator(Map<String, List<String>> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * Setting the next validation handler
     *
     * @param nextValidator next handler
     */
    @Override
    public CarValidator setNext(CarValidator nextValidator) {
        this.nextValidator = nextValidator;
        return this.nextValidator;
    }
}
