package com.arakviel.carrepair.domain.validator.order;

import com.arakviel.carrepair.domain.impl.Order;
import com.arakviel.carrepair.domain.validator.OrderValidator;
import com.arakviel.carrepair.domain.validator.ValidatorChain;

public class OrderValidatorChain extends ValidatorChain {

    OrderValidator firstValidator;

    @Override
    protected void validateChain() {
        firstValidator = new ClientOrderValidator(validationMessages);
        firstValidator
                .setNext(new CarOrderValidator(validationMessages))
                .setNext(new DiscountOrderValidator(validationMessages))
                .setNext(new PriceWholePartOrderValidator(validationMessages))
                .setNext(new PriceDecimalPartOrderValidator(validationMessages))
                .setNext(new PaymentTypeOrderValidator(validationMessages))
                .setNext(new PaymentAtOrderValidator(validationMessages));
    }

    public boolean validate(Order order) {
        validationMessages.clear();
        firstValidator.validate(order);
        return validationMessages.size() == 0;
    }

    private static class SingletonHolder {
        public static final OrderValidatorChain INSTANCE = new OrderValidatorChain();
    }

    public static OrderValidatorChain getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
