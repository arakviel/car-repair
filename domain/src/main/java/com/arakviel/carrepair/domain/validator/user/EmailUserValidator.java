package com.arakviel.carrepair.domain.validator.user;

import com.arakviel.carrepair.domain.impl.User;
import com.arakviel.carrepair.domain.validator.UserValidator;
import com.arakviel.carrepair.domain.validator.util.EmailValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class EmailUserValidator extends UserValidator {

    EmailUserValidator(Map<String, List<String>> validationMessages) {
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
        String email = user.getEmail();
        List<String> messages = new ArrayList<>(3);

        if (Objects.isNull(email) || email.isBlank()) {
            messages.add("Не може бути порожнім");
        } else {
            if (!EmailValidator.getInstance().test(email)) {
                messages.add("Значення не є коректним");
            }
            if (email.trim().length() > 256) {
                messages.add("Повинен містити не більше 256 символів");
            }
        }

        if (!messages.isEmpty()) {
            validationMessages.put("email", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(user);
        }
        return validateResult;
    }
}
