package com.arakviel.carrepair.domain.validator;

import com.arakviel.carrepair.domain.impl.Role;
import java.util.List;
import java.util.Map;

public abstract class RoleValidator implements Validator<Role, RoleValidator> {

    protected RoleValidator nextValidator;
    protected Map<String, List<String>> validationMessages;

    protected RoleValidator(Map<String, List<String>> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * Setting the next validation handler
     *
     * @param nextValidator next handler
     */
    @Override
    public RoleValidator setNext(RoleValidator nextValidator) {
        this.nextValidator = nextValidator;
        return this.nextValidator;
    }
}
