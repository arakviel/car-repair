package com.arakviel.carrepair.persistence.entity.impl;

import com.arakviel.carrepair.persistence.entity.BaseEntity;
import com.arakviel.carrepair.persistence.entity.Entity;
import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.UUID;

public class PayrollEntity extends BaseEntity<UUID> implements Entity {

    private EmployeeEntity employeeEntity;
    private PeriodType periodType;
    private int hourCount;
    private Money salary;
    private LocalDateTime paymentAt;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    private PayrollEntity(
            UUID id,
            EmployeeEntity employeeEntity,
            PeriodType periodType,
            int hourCount,
            Money salary,
            LocalDateTime paymentAt) {
        super(id);
        this.employeeEntity = employeeEntity;
        this.periodType = periodType;
        this.hourCount = hourCount;
        this.salary = salary;
        this.paymentAt = paymentAt;
    }

    public static PayrollEntityBuilderId builder() {
        return id -> employeeEntity -> periodType -> hourCount -> salary ->
                paymentAt -> () -> new PayrollEntity(id, employeeEntity, periodType, hourCount, salary, paymentAt);
    }

    @FunctionalInterface
    public interface PayrollEntityBuilderId {
        PayrollEntityBuilderEmployeeEntity id(UUID id);
    }

    @FunctionalInterface
    public interface PayrollEntityBuilderEmployeeEntity {
        PayrollEntityBuilderPeriodType employeeEntity(EmployeeEntity employeeEntity);
    }

    @FunctionalInterface
    public interface PayrollEntityBuilderPeriodType {
        PayrollEntityBuilderHourCount periodType(PeriodType periodType);
    }

    @FunctionalInterface
    public interface PayrollEntityBuilderHourCount {
        PayrollEntityBuilderSalary hourCount(int hourCount);
    }

    @FunctionalInterface
    public interface PayrollEntityBuilderSalary {
        PayrollEntityBuilderPaymentAt salary(Money salary);
    }

    @FunctionalInterface
    public interface PayrollEntityBuilderPaymentAt {
        PayrollEntityBuilder paymentAt(LocalDateTime paymentAt);
    }

    @FunctionalInterface
    public interface PayrollEntityBuilder {
        PayrollEntity build();
    }

    public EmployeeEntity getEmployeeEntity() {
        return employeeEntity;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public int getHourCount() {
        return hourCount;
    }

    public Money getSalary() {
        return salary;
    }

    public LocalDateTime getPaymentAt() {
        return paymentAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PayrollEntity.class.getSimpleName() + "[", "]")
                .add("employeeEntity=" + employeeEntity)
                .add("periodType=" + periodType)
                .add("hourCount=" + hourCount)
                .add("salary=" + salary)
                .add("paymentAt=" + paymentAt)
                .add("updatedAt=" + updatedAt)
                .add("createdAt=" + createdAt)
                .add("id=" + id)
                .toString();
    }

    public enum PeriodType {
        QUARTERLY("квартальний"),
        MONTHLY("місячний"),
        WEEKLY("тижневий"),
        DAILY("денний");

        private final String value;

        PeriodType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
