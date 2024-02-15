package com.arakviel.carrepair.domain.validator;

import com.arakviel.carrepair.domain.impl.Position;
import java.util.List;
import java.util.Map;

public abstract class PositionValidator implements Validator<Position, PositionValidator> {

    protected PositionValidator nextValidator;
    protected Map<String, List<String>> validationMessages;

    protected PositionValidator(Map<String, List<String>> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * Setting the next validation handler
     *
     * @param nextValidator next handler
     */
    @Override
    public PositionValidator setNext(PositionValidator nextValidator) {
        this.nextValidator = nextValidator;
        return this.nextValidator;
    }
}
