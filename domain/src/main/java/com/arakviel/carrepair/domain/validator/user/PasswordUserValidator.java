package com.arakviel.carrepair.domain.validator.user;

import com.arakviel.carrepair.domain.impl.User;
import com.arakviel.carrepair.domain.validator.UserValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class PasswordUserValidator extends UserValidator {

    PasswordUserValidator(Map<String, List<String>> validationMessages) {
        super(validationMessages);
    }

    /**
     * A validation process that writes errors to the validationMessages collection of the object's
     * user.
     *
     * @param user current user to validate
     */
    @Override
    public boolean validate(User user) {
        boolean validateResult = true;
        String password = user.getPassword();
        List<String> messages = new ArrayList<>(3);

        if (Objects.isNull(password) || password.isBlank()) {
            messages.add("Не може бути порожнім");
        } else {
            if (!password.matches("^[a-zA-Z0-9_-]+$")) {
                messages.add("Значення не є коректним. Лише латиниця, цифри та символ _ та -");
            }
            if (password.trim().length() < 3) {
                messages.add("Повинен містити більше 4 символів");
            }
            if (password.trim().length() > 32) {
                messages.add("Повинен містити не більше 32 символів");
            }
        }

        if (!messages.isEmpty()) {
            validationMessages.put("password", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(user);
        }
        return validateResult;
    }
}
