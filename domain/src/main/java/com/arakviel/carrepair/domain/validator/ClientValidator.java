package com.arakviel.carrepair.domain.validator;

import com.arakviel.carrepair.domain.impl.Client;
import java.util.List;
import java.util.Map;

public abstract class ClientValidator implements Validator<Client, ClientValidator> {

    protected ClientValidator nextValidator;
    protected Map<String, List<String>> validationMessages;

    protected ClientValidator(Map<String, List<String>> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * Setting the next validation handler
     *
     * @param nextValidator next handler
     */
    @Override
    public ClientValidator setNext(ClientValidator nextValidator) {
        this.nextValidator = nextValidator;
        return this.nextValidator;
    }
}
