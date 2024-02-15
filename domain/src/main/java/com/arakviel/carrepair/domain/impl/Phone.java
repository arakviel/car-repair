package com.arakviel.carrepair.domain.impl;

import com.arakviel.carrepair.domain.BaseDomain;
import com.arakviel.carrepair.domain.Domain;
import java.util.UUID;

public class Phone extends BaseDomain<UUID> implements Domain {

    private Employee employee;
    private PhoneType phoneType;
    private String value;

    private Phone(Employee employee, PhoneType phoneType, String value) {
        this.employee = employee;
        this.phoneType = phoneType;
        this.value = value;
    }

    public static PhoneBuilderEmployee builder() {
        return employee -> phoneType -> value -> () -> new Phone(employee, phoneType, value);
    }

    @FunctionalInterface
    public interface PhoneBuilderEmployee {
        PhoneBuilderPhoneType employee(Employee employee);
    }

    @FunctionalInterface
    public interface PhoneBuilderPhoneType {
        PhoneBuilderValue phoneType(PhoneType phoneType);
    }

    @FunctionalInterface
    public interface PhoneBuilderValue {
        PhoneBuilder value(String value);
    }

    @FunctionalInterface
    public interface PhoneBuilder {
        Phone build();
    }

    public Employee getEmployee() {
        return employee;
    }

    public PhoneType getPhoneType() {
        return phoneType;
    }

    public String getValue() {
        return value;
    }

    public enum PhoneType {
        WORK("робочий"),
        PERSONAL("особистий");

        private final String type;

        PhoneType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
