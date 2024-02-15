package com.arakviel.carrepair.domain.impl;

import com.arakviel.carrepair.domain.BaseDomain;
import com.arakviel.carrepair.domain.Domain;
import java.time.LocalDateTime;
import java.util.UUID;

public class Payroll extends BaseDomain<UUID> implements Domain {

    private Employee employee;
    private PeriodType periodType;
    private int hourCount;
    private Money salary;
    private LocalDateTime paymentAt;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    private Payroll(Employee employee, PeriodType periodType, int hourCount, Money salary, LocalDateTime paymentAt) {
        this.employee = employee;
        this.periodType = periodType;
        this.hourCount = hourCount;
        this.salary = salary;
        this.paymentAt = paymentAt;
    }

    public static PayrollBuilderEmployee builder() {
        return employee -> periodType -> hourCount ->
                salary -> paymentAt -> () -> new Payroll(employee, periodType, hourCount, salary, paymentAt);
    }

    @FunctionalInterface
    public interface PayrollBuilderId {
        PayrollBuilderEmployee id(UUID id);
    }

    @FunctionalInterface
    public interface PayrollBuilderEmployee {
        PayrollBuilderPeriodType employee(Employee employee);
    }

    @FunctionalInterface
    public interface PayrollBuilderPeriodType {
        PayrollBuilderHourCount periodType(PeriodType periodType);
    }

    @FunctionalInterface
    public interface PayrollBuilderHourCount {
        PayrollBuilderSalary hourCount(int hourCount);
    }

    @FunctionalInterface
    public interface PayrollBuilderSalary {
        PayrollBuilderPaymentAt salary(Money salary);
    }

    @FunctionalInterface
    public interface PayrollBuilderPaymentAt {
        PayrollBuilder paymentAt(LocalDateTime paymentAt);
    }

    @FunctionalInterface
    public interface PayrollBuilder {
        Payroll build();
    }

    public Employee getEmployee() {
        return employee;
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
