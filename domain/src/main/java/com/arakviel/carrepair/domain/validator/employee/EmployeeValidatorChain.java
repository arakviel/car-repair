package com.arakviel.carrepair.domain.validator.employee;

import com.arakviel.carrepair.domain.impl.Employee;
import com.arakviel.carrepair.domain.validator.EmployeeValidator;
import com.arakviel.carrepair.domain.validator.ValidatorChain;

public class EmployeeValidatorChain extends ValidatorChain {

    EmployeeValidator firstValidator;

    @Override
    protected void validateChain() {
        firstValidator = new AddressEmployeeValidator(validationMessages);
        firstValidator
                .setNext(new WorkroomEmployeeValidator(validationMessages))
                .setNext(new PositionEmployeeValidator(validationMessages))
                .setNext(new FirstNameEmployeeValidator(validationMessages))
                .setNext(new LastNameEmployeeValidator(validationMessages))
                .setNext(new MiddleNameEmployeeValidator(validationMessages))
                .setNext(new PhotoEmployeeValidator(validationMessages))
                .setNext(new PassportDocCopyEmployeeValidator(validationMessages));
    }

    public boolean validate(Employee employee) {
        validationMessages.clear();
        firstValidator.validate(employee);
        return validationMessages.size() == 0;
    }

    private static class SingletonHolder {
        public static final EmployeeValidatorChain INSTANCE = new EmployeeValidatorChain();
    }

    public static EmployeeValidatorChain getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
