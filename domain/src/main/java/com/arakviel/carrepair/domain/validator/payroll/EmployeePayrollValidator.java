package com.arakviel.carrepair.domain.validator.payroll;

import com.arakviel.carrepair.domain.impl.Payroll;
import com.arakviel.carrepair.domain.validator.PayrollValidator;
import com.arakviel.carrepair.domain.validator.util.DomainRequireValidator;
import java.util.List;
import java.util.Map;

class EmployeePayrollValidator extends PayrollValidator {

    EmployeePayrollValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages collection of the object's
     * payroll.
     *
     * @param payroll current payroll to validate
     */
    @Override
    public boolean validate(Payroll payroll) {
        boolean validateResult = true;
        List<String> messages = DomainRequireValidator.getInstance()
                .getErrorMessages(payroll.getEmployee(), payroll.getEmployee().getId());

        if (!messages.isEmpty()) {
            validationMessages.put("employee", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(payroll);
        }

        return validateResult;
    }
}
