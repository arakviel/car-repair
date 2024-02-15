package com.arakviel.carrepair.domain.validator;

import com.arakviel.carrepair.domain.impl.Payroll;
import java.util.List;
import java.util.Map;

public abstract class PayrollValidator implements Validator<Payroll, PayrollValidator> {

    protected PayrollValidator nextValidator;
    protected Map<String, List<String>> validationMessages;

    protected PayrollValidator(Map<String, List<String>> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * Setting the next validation handler
     *
     * @param nextValidator next handler
     */
    @Override
    public PayrollValidator setNext(PayrollValidator nextValidator) {
        this.nextValidator = nextValidator;
        return this.nextValidator;
    }
}
