package com.arakviel.carrepair.domain.validator.workroom;

import com.arakviel.carrepair.domain.impl.Workroom;
import com.arakviel.carrepair.domain.validator.ValidatorChain;
import com.arakviel.carrepair.domain.validator.WorkroomValidator;

public class WorkroomValidatorChain extends ValidatorChain {

    WorkroomValidator firstValidator;

    @Override
    protected void validateChain() {
        firstValidator = new AddressWorkroomValidator(validationMessages);
        firstValidator
                .setNext(new NameWorkroomValidator(validationMessages))
                .setNext(new PhotoWorkroomValidator(validationMessages))
                .setNext(new DescriptionWorkroomValidator(validationMessages));
    }

    public boolean validate(Workroom workroom) {
        validationMessages.clear();
        firstValidator.validate(workroom);
        return validationMessages.size() == 0;
    }

    private static class SingletonHolder {
        public static final WorkroomValidatorChain INSTANCE = new WorkroomValidatorChain();
    }

    public static WorkroomValidatorChain getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
