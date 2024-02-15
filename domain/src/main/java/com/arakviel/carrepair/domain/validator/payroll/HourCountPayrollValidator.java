package com.arakviel.carrepair.domain.validator.payroll;

import com.arakviel.carrepair.domain.impl.Payroll;
import com.arakviel.carrepair.domain.validator.PayrollValidator;
import com.arakviel.carrepair.domain.validator.util.NumberValidator;
import java.util.List;
import java.util.Map;

class HourCountPayrollValidator extends PayrollValidator {

    protected HourCountPayrollValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages map collection.
     *
     * @param payroll current payroll to validate
     */
    @Override
    public boolean validate(Payroll payroll) {
        boolean validateResult = true;
        int hoursInYear = 8784;
        List<String> messages =
                NumberValidator.getInstance().getErrorMessages(payroll.getHourCount(), 1, hoursInYear, true);

        if (!messages.isEmpty()) {
            validationMessages.put("hourCount", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(payroll);
        }

        return validateResult;
    }
}
