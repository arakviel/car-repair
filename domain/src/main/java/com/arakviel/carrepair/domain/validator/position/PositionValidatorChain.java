package com.arakviel.carrepair.domain.validator.position;

import com.arakviel.carrepair.domain.impl.Position;
import com.arakviel.carrepair.domain.validator.PositionValidator;
import com.arakviel.carrepair.domain.validator.ValidatorChain;

public class PositionValidatorChain extends ValidatorChain {

    PositionValidator firstValidator;

    @Override
    protected void validateChain() {
        firstValidator = new NamePositionValidator(validationMessages);
        firstValidator
                .setNext(new DescriptionPositionValidator(validationMessages))
                .setNext(new CurrencyPositionValidator(validationMessages))
                .setNext(new SalaryPerHourWholePartPositionValidator(validationMessages))
                .setNext(new SalaryPerHourDecimalPartPositionValidator(validationMessages));
    }

    public boolean validate(Position position) {
        validationMessages.clear();
        firstValidator.validate(position);
        return validationMessages.size() == 0;
    }

    private static class SingletonHolder {
        public static final PositionValidatorChain INSTANCE = new PositionValidatorChain();
    }

    public static PositionValidatorChain getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
