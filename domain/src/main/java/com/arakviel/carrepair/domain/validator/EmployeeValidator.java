package com.arakviel.carrepair.domain.validator;

import com.arakviel.carrepair.domain.impl.Employee;
import java.util.List;
import java.util.Map;

public abstract class EmployeeValidator implements Validator<Employee, EmployeeValidator> {

    protected EmployeeValidator nextValidator;
    protected Map<String, List<String>> validationMessages;

    protected EmployeeValidator(Map<String, List<String>> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * Setting the next validation handler
     *
     * @param nextValidator next handler
     */
    @Override
    public EmployeeValidator setNext(EmployeeValidator nextValidator) {
        this.nextValidator = nextValidator;
        return this.nextValidator;
    }
}
