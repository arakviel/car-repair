package com.arakviel.carrepair.domain.validator.role;

import com.arakviel.carrepair.domain.impl.Role;
import com.arakviel.carrepair.domain.validator.RoleValidator;
import com.arakviel.carrepair.domain.validator.util.ObjectRequireValidator;
import java.util.List;
import java.util.Map;

// Можливо це бредово :)
class CanEditRoleValidator extends RoleValidator {

    private String canEditField;

    protected CanEditRoleValidator(Map<String, List<String>> validationMessages, String canEditField) {
        super(validationMessages);
        this.canEditField = canEditField;
    }

    /**
     * A validation process that writes errors to the validationMessages map collection.
     *
     * @param role current role to validate
     */
    @Override
    public boolean validate(Role role) {
        boolean validateResult = true;
        List<String> messages = ObjectRequireValidator.getInstance()
                .getErrorMessages(
                        switch (canEditField) {
                            case "users" -> role.canEditUsers();
                            case "spares" -> role.canEditSpares();
                            case "clients" -> role.canEditClients();
                            case "services" -> role.canEditServices();
                            case "orders" -> role.canEditOrders();
                            case "payrolls" -> role.canEditPayrolls();
                            default -> throw new IllegalStateException("Unexpected value: " + canEditField);
                        });

        if (!messages.isEmpty()) {
            validationMessages.put("paymentAt", messages);
            validateResult = false;
        }

        if (nextValidator != null) {
            return nextValidator.validate(role);
        }

        return validateResult;
    }
}
