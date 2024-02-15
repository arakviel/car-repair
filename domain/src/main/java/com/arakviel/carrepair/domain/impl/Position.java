package com.arakviel.carrepair.domain.impl;

import com.arakviel.carrepair.domain.BaseDomain;
import com.arakviel.carrepair.domain.Domain;
import java.util.StringJoiner;

public class Position extends BaseDomain<Integer> implements Domain {

    private String name;
    private String description;
    private Currency currency;
    private Money salaryPerHour;
    private Role role;

    private Position(String name, Currency currency, Money salaryPerHour) {
        this.name = name;
        this.currency = currency;
        this.salaryPerHour = salaryPerHour;
    }

    public static PositionBuilderName builder() {
        return name -> currency -> salaryPerHour -> () -> new Position(name, currency, salaryPerHour);
    }

    @FunctionalInterface
    public interface PositionBuilderName {

        PositionBuilderCurrency name(String name);
    }

    @FunctionalInterface
    public interface PositionBuilderCurrency {

        PositionBuilderSalaryPerHour currency(Currency currency);
    }

    @FunctionalInterface
    public interface PositionBuilderSalaryPerHour {
        PositionBuilder salaryPerHour(Money salaryPerHour);
    }

    @FunctionalInterface
    public interface PositionBuilder {
        Position build();
    }

    public String getName() {
        return name;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Money getSalaryPerHour() {
        return salaryPerHour;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Position.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("description='" + description + "'")
                .add("currency=" + currency)
                .add("salaryPerHour=" + salaryPerHour)
                .add("role=" + role)
                .add("id=" + id)
                .toString();
    }
}
