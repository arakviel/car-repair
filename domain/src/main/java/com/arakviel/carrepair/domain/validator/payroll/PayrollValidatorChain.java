package com.arakviel.carrepair.domain.validator.payroll;

import com.arakviel.carrepair.domain.impl.Payroll;
import com.arakviel.carrepair.domain.validator.PayrollValidator;
import com.arakviel.carrepair.domain.validator.ValidatorChain;

public class PayrollValidatorChain extends ValidatorChain {

    PayrollValidator firstValidator;

    @Override
    protected void validateChain() {
        firstValidator = new EmployeePayrollValidator(validationMessages);
        firstValidator
                .setNext(new PeriodTypePayrollValidator(validationMessages))
                .setNext(new HourCountPayrollValidator(validationMessages))
                .setNext(new SalaryWholePartPayrollValidator(validationMessages))
                .setNext(new SalaryDecimalPartPayrollValidator(validationMessages))
                .setNext(new PaymentsAtPayrollValidator(validationMessages));
    }

    public boolean validate(Payroll payroll) {
        validationMessages.clear();
        firstValidator.validate(payroll);
        return validationMessages.size() == 0;
    }

    private static class SingletonHolder {
        public static final PayrollValidatorChain INSTANCE = new PayrollValidatorChain();
    }

    public static PayrollValidatorChain getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
