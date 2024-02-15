package com.arakviel.carrepair.domain.validator.car;

import com.arakviel.carrepair.domain.impl.Car;
import com.arakviel.carrepair.domain.validator.CarValidator;
import com.arakviel.carrepair.domain.validator.ValidatorChain;

public class CarValidatorChain extends ValidatorChain {
    CarValidator firstValidator;

    @Override
    protected void validateChain() {
        firstValidator = new ModelCarValidator(validationMessages);
        firstValidator
                .setNext(new NumberCarValidator(validationMessages))
                .setNext(new YearCarValidator(validationMessages))
                .setNext(new EngineTypeCarValidator(validationMessages))
                .setNext(new MileageCarValidator(validationMessages))
                .setNext(new ColorCarValidator(validationMessages));
    }

    public boolean validate(Car car) {
        validationMessages.clear();
        firstValidator.validate(car);
        return validationMessages.size() == 0;
    }

    private static class SingletonHolder {
        public static final CarValidatorChain INSTANCE = new CarValidatorChain();
    }

    public static CarValidatorChain getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
