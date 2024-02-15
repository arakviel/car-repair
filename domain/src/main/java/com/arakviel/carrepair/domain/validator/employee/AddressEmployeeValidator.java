package com.arakviel.carrepair.domain.validator.employee;

import com.arakviel.carrepair.domain.impl.Employee;
import com.arakviel.carrepair.domain.validator.EmployeeValidator;
import com.arakviel.carrepair.domain.validator.util.DomainRequireValidator;
import java.util.List;
import java.util.Map;

class AddressEmployeeValidator extends EmployeeValidator {

    protected AddressEmployeeValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages map collection.
     *
     * @param employee current employee to validate
     */
    @Override
    public boolean validate(Employee employee) {
        boolean validateResult = true;
        List<String> messages = DomainRequireValidator.getInstance()
                .getErrorMessages(employee.getAddress(), employee.getAddress().getId());

        if (!messages.isEmpty()) {
            validationMessages.put("address", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(employee);
        }

        return validateResult;
    }
}
