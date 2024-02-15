package com.arakviel.carrepair.persistence.entity.impl;

import com.arakviel.carrepair.persistence.entity.BaseEntity;
import com.arakviel.carrepair.persistence.entity.Entity;
import java.util.StringJoiner;
import java.util.UUID;

public class PhoneEntity extends BaseEntity<UUID> implements Entity {

    private EmployeeEntity employeeEntity;
    private PhoneType phoneType;
    private String value;

    private PhoneEntity(UUID id, EmployeeEntity employeeEntity, PhoneType phoneType, String value) {
        super(id);
        this.employeeEntity = employeeEntity;
        this.phoneType = phoneType;
        this.value = value;
    }

    public static PhoneEntityBuilderId builder() {
        return id ->
                employeeEntity -> phoneType -> value -> () -> new PhoneEntity(id, employeeEntity, phoneType, value);
    }

    @FunctionalInterface
    public interface PhoneEntityBuilderId {
        PhoneEntityBuilderEmployeeEntity id(UUID id);
    }

    @FunctionalInterface
    public interface PhoneEntityBuilderEmployeeEntity {
        PhoneEntityBuilderPhoneType employeeEntity(EmployeeEntity employeeEntity);
    }

    @FunctionalInterface
    public interface PhoneEntityBuilderPhoneType {
        PhoneEntityBuilderValue phoneType(PhoneType phoneType);
    }

    @FunctionalInterface
    public interface PhoneEntityBuilderValue {
        PhoneEntityBuilder value(String value);
    }

    @FunctionalInterface
    public interface PhoneEntityBuilder {
        PhoneEntity build();
    }

    public EmployeeEntity getEmployeeEntity() {
        return employeeEntity;
    }

    public PhoneType getPhoneType() {
        return phoneType;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PhoneEntity.class.getSimpleName() + "[", "]")
                .add("employeeEntity=" + employeeEntity)
                .add("phoneType=" + phoneType)
                .add("value='" + value + "'")
                .add("id=" + id)
                .toString();
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
