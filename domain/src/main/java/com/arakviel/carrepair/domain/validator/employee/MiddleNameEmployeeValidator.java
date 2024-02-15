package com.arakviel.carrepair.domain.validator.employee;

import com.arakviel.carrepair.domain.impl.Employee;
import com.arakviel.carrepair.domain.validator.EmployeeValidator;
import com.arakviel.carrepair.domain.validator.util.NameValidator;
import java.util.List;
import java.util.Map;

class MiddleNameEmployeeValidator extends EmployeeValidator {

    MiddleNameEmployeeValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages collection of the object's
     * employee.
     *
     * @param employee current employee to validate
     */
    @Override
    public boolean validate(Employee employee) {
        boolean validateResult = true;
        List<String> messages = NameValidator.getInstance().getErrorMessages(employee.getMiddleName(), false, true);

        if (!messages.isEmpty()) {
            validationMessages.put("middleName", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(employee);
        }

        return validateResult;
    }
}
