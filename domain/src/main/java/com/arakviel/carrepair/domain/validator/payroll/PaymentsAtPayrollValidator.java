package com.arakviel.carrepair.domain.validator.payroll;

import com.arakviel.carrepair.domain.impl.Payroll;
import com.arakviel.carrepair.domain.validator.PayrollValidator;
import com.arakviel.carrepair.domain.validator.util.ObjectRequireValidator;
import java.util.List;
import java.util.Map;

class PaymentsAtPayrollValidator extends PayrollValidator {

    protected PaymentsAtPayrollValidator(Map<String, List<String>> validationMessages) {
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
        List<String> messages = ObjectRequireValidator.getInstance().getErrorMessages(payroll.getPaymentAt());

        if (!messages.isEmpty()) {
            validationMessages.put("paymentAt", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(payroll);
        }

        return validateResult;
    }
}
