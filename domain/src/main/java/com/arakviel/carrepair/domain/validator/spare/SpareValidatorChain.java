package com.arakviel.carrepair.domain.validator.spare;

import com.arakviel.carrepair.domain.impl.Spare;
import com.arakviel.carrepair.domain.validator.SpareValidator;
import com.arakviel.carrepair.domain.validator.ValidatorChain;

public class SpareValidatorChain extends ValidatorChain {

    SpareValidator firstValidator;

    @Override
    protected void validateChain() {
        firstValidator = new WorkroomSpareValidator(validationMessages);
        firstValidator
                .setNext(new NameSpareValidator(validationMessages))
                .setNext(new DescriptionSpareValidator(validationMessages))
                // .setNext(new PhotoSpareValidator(validationMessages))
                .setNext(new PriceWholePartSpareValidator(validationMessages))
                .setNext(new PriceDecimalPartSpareValidator(validationMessages))
                .setNext(new QuantityInStockSpareValidator(validationMessages));
    }

    public boolean validate(Spare spare) {
        validationMessages.clear();
        firstValidator.validate(spare);
        return validationMessages.size() == 0;
    }

    private static class SingletonHolder {
        public static final SpareValidatorChain INSTANCE = new SpareValidatorChain();
    }

    public static SpareValidatorChain getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
