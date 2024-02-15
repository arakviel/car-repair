package com.arakviel.carrepair.domain.validator;

import com.arakviel.carrepair.domain.impl.User;
import java.util.List;
import java.util.Map;

public abstract class UserValidator implements Validator<User, UserValidator> {

    protected UserValidator nextValidator;
    protected Map<String, List<String>> validationMessages;

    protected UserValidator(Map<String, List<String>> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * Setting the next validation handler
     *
     * @param nextValidator next handler
     */
    @Override
    public UserValidator setNext(UserValidator nextValidator) {
        this.nextValidator = nextValidator;
        return this.nextValidator;
    }
}
