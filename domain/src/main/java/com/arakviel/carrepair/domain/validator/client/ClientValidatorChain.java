package com.arakviel.carrepair.domain.validator.client;

import com.arakviel.carrepair.domain.impl.Client;
import com.arakviel.carrepair.domain.validator.ClientValidator;
import com.arakviel.carrepair.domain.validator.ValidatorChain;

public class ClientValidatorChain extends ValidatorChain {
    ClientValidator firstValidator;

    @Override
    protected void validateChain() {
        firstValidator = new PhoneClientValidator(validationMessages);
        firstValidator
                .setNext(new EmailClientValidator(validationMessages))
                .setNext(new FirstNameClientValidator(validationMessages))
                .setNext(new LastNameClientValidator(validationMessages))
                .setNext(new MiddleNameClientValidator(validationMessages));
    }

    public boolean validate(Client client) {
        validationMessages.clear();
        firstValidator.validate(client);
        return validationMessages.size() == 0;
    }

    private static class SingletonHolder {
        public static final ClientValidatorChain INSTANCE = new ClientValidatorChain();
    }

    public static ClientValidatorChain getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
