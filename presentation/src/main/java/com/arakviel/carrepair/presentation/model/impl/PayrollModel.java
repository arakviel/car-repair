package com.arakviel.carrepair.presentation.model.impl;

import com.arakviel.carrepair.presentation.model.BaseModel;
import com.arakviel.carrepair.presentation.model.Model;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.UUID;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class PayrollModel extends BaseModel<UUID> implements Model, Cloneable {

    private ObjectProperty<EmployeeModel> employeeModel = new SimpleObjectProperty<>();
    private ObjectProperty<PeriodType> periodType = new SimpleObjectProperty<>();
    private IntegerProperty hourCount = new SimpleIntegerProperty();
    private ObjectProperty<MoneyModel> salary = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> paymentAt = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();

    private PayrollModel(
            EmployeeModel employeeModel, PeriodType periodType, int hourCount, MoneyModel salary, LocalDate paymentAt) {
        this.id = new SimpleObjectProperty<>();
        this.employeeModel.set(employeeModel);
        this.periodType.set(periodType);
        this.hourCount.set(hourCount);
        this.salary.set(salary);
        this.paymentAt.set(paymentAt);
    }

    public static PayrollModelBuilderEmployee builder() {
        return employeeModel -> periodType -> hourCount ->
                salary -> paymentAt -> () -> new PayrollModel(employeeModel, periodType, hourCount, salary, paymentAt);
    }

    @FunctionalInterface
    public interface PayrollModelBuilderId {
        PayrollModelBuilderEmployee id(UUID id);
    }

    @FunctionalInterface
    public interface PayrollModelBuilderEmployee {
        PayrollModelBuilderPeriodType employeeModel(EmployeeModel employeeModel);
    }

    @FunctionalInterface
    public interface PayrollModelBuilderPeriodType {
        PayrollModelBuilderHourCount periodType(PeriodType periodType);
    }

    @FunctionalInterface
    public interface PayrollModelBuilderHourCount {
        PayrollModelBuilderSalary hourCount(int hourCount);
    }

    @FunctionalInterface
    public interface PayrollModelBuilderSalary {
        PayrollModelBuilderPaymentAt salary(MoneyModel salary);
    }

    @FunctionalInterface
    public interface PayrollModelBuilderPaymentAt {
        PayrollModelBuilder paymentAt(LocalDate paymentAt);
    }

    @FunctionalInterface
    public interface PayrollModelBuilder {
        PayrollModel build();
    }

    public EmployeeModel getEmployeeModel() {
        return employeeModel.get();
    }

    public ObjectProperty<EmployeeModel> employeeModelProperty() {
        return employeeModel;
    }

    public void setEmployeeModel(EmployeeModel employeeModel) {
        this.employeeModel.set(employeeModel);
    }

    public PeriodType getPeriodType() {
        return periodType.get();
    }

    public ObjectProperty<PeriodType> periodTypeProperty() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType.set(periodType);
    }

    public int getHourCount() {
        return hourCount.get();
    }

    public IntegerProperty hourCountProperty() {
        return hourCount;
    }

    public void setHourCount(int hourCount) {
        this.hourCount.set(hourCount);
    }

    public MoneyModel getSalary() {
        return salary.get();
    }

    public ObjectProperty<MoneyModel> salaryProperty() {
        return salary;
    }

    public void setSalary(MoneyModel salary) {
        this.salary.set(salary);
    }

    public LocalDate getPaymentAt() {
        return paymentAt.get();
    }

    public ObjectProperty<LocalDate> paymentAtProperty() {
        return paymentAt;
    }

    public void setPaymentAt(LocalDate paymentAt) {
        this.paymentAt.set(paymentAt);
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt.get();
    }

    public ObjectProperty<LocalDateTime> updatedAtProperty() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt.set(updatedAt);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }

    public ObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt.set(createdAt);
    }

    @Override
    public PayrollModel clone() {
        try {
            PayrollModel cloned = (PayrollModel) super.clone();
            cloned.id = new SimpleObjectProperty<>(this.id.get());
            cloned.employeeModel = new SimpleObjectProperty<>(this.employeeModel.get());
            cloned.periodType = new SimpleObjectProperty<>(this.periodType.get());
            cloned.hourCount = new SimpleIntegerProperty(this.hourCount.get());
            cloned.salary = new SimpleObjectProperty<>(this.salary.get());
            cloned.paymentAt = new SimpleObjectProperty<>(this.paymentAt.get());
            cloned.updatedAt = new SimpleObjectProperty<>(this.updatedAt.get());
            cloned.createdAt = new SimpleObjectProperty<>(this.createdAt.get());

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PayrollModel.class.getSimpleName() + "[", "]")
                .add("employeeModel=" + employeeModel)
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
