package com.arakviel.carrepair.domain.validator.user;

import com.arakviel.carrepair.domain.impl.User;
import com.arakviel.carrepair.domain.validator.UserValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class LoginUserValidator extends UserValidator {

    LoginUserValidator(Map<String, List<String>> validationMessages) {
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
        String login = user.getLogin();
        List<String> messages = new ArrayList<>(4);

        if (Objects.isNull(login) || login.isBlank()) {
            messages.add("Не може бути порожнім");
        } else {
            if (!login.matches("^[a-zA-Z0-9_-]+$")) {
                messages.add("Значення не є коректним. Лише латиниця, цифри та символи _-");
            }
            if (login.trim().length() < 3) {
                messages.add("Повинен містити більше 4 символів");
            }
            if (login.trim().length() > 32) {
                messages.add("Повинен містити не більше 32 символів");
            }
        }

        if (!messages.isEmpty()) {
            validationMessages.put("login", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(user);
        }
        return validateResult;
    }
}
