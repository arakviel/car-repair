package com.arakviel.carrepair.domain.validator;

import com.arakviel.carrepair.domain.impl.Workroom;
import java.util.List;
import java.util.Map;

public abstract class WorkroomValidator implements Validator<Workroom, WorkroomValidator> {

    protected WorkroomValidator nextValidator;
    protected Map<String, List<String>> validationMessages;

    protected WorkroomValidator(Map<String, List<String>> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * Setting the next validation handler
     *
     * @param nextValidator next handler
     */
    @Override
    public WorkroomValidator setNext(WorkroomValidator nextValidator) {
        this.nextValidator = nextValidator;
        return this.nextValidator;
    }
}
