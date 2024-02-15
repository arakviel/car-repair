package com.arakviel.carrepair.domain.validator.util;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * The EmailValidator class implements a Predicate interface to validate email addresses using a
 * regular expression. Example usage: <code>
 * EmailValidator emailValidator = new EmailValidator();
 * boolean isValid = emailValidator.test("example@example.com");
 * </code>
 */
public class EmailValidator implements Predicate<String> {

    private static final Predicate<String> IS_EMAIL_VALID = Pattern.compile(
                    "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
            .asPredicate();

    /**
     * Checks for the correct email format using a regular expression.
     *
     * @param email email to be verified
     * @return true, if the email matches the format specified by the regular expression; false, if
     *     the email does not match the format
     */
    @Override
    public boolean test(String email) {
        return IS_EMAIL_VALID.test(email);
    }

    private EmailValidator() {}

    private static class SingletonHolder {
        public static final EmailValidator INSTANCE = new EmailValidator();
    }

    public static EmailValidator getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
