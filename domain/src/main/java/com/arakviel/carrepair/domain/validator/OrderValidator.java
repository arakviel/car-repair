package com.arakviel.carrepair.domain.validator;

import com.arakviel.carrepair.domain.impl.Order;
import java.util.List;
import java.util.Map;

public abstract class OrderValidator implements Validator<Order, OrderValidator> {

    protected OrderValidator nextValidator;
    protected Map<String, List<String>> validationMessages;

    protected OrderValidator(Map<String, List<String>> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * Setting the next validation handler
     *
     * @param nextValidator next handler
     */
    @Override
    public OrderValidator setNext(OrderValidator nextValidator) {
        this.nextValidator = nextValidator;
        return this.nextValidator;
    }
}
