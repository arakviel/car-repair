package com.arakviel.carrepair.presentation.mapper.impl;

import com.arakviel.carrepair.domain.impl.Money;
import com.arakviel.carrepair.domain.impl.Payroll;
import com.arakviel.carrepair.domain.impl.Payroll.PeriodType;
import com.arakviel.carrepair.presentation.mapper.ModelMapper;
import com.arakviel.carrepair.presentation.model.impl.MoneyModel;
import com.arakviel.carrepair.presentation.model.impl.PayrollModel;
import java.util.Objects;

public class PayrollModelMapper implements ModelMapper<Payroll, PayrollModel> {

    @Override
    public PayrollModel toModel(Payroll payroll) {
        var employeeModelMapper = ModelMapperFactory.getInstance().getEmployeeModelMapper();
        PayrollModel payrollModel = PayrollModel.builder()
                .employeeModel(employeeModelMapper.toModel(payroll.getEmployee()))
                .periodType(
                        PayrollModel.PeriodType.valueOf(payroll.getPeriodType().toString()))
                .hourCount(payroll.getHourCount())
                .salary(new MoneyModel(
                        payroll.getSalary().wholePart(), payroll.getSalary().decimalPart()))
                .paymentAt(
                        Objects.nonNull(payroll.getPaymentAt().toLocalDate())
                                ? payroll.getPaymentAt().toLocalDate()
                                : null)
                .build();
        payrollModel.setId(payroll.getId());
        payrollModel.setUpdatedAt(payroll.getUpdatedAt());
        payrollModel.setCreatedAt(payroll.getCreatedAt());
        return payrollModel;
    }

    @Override
    public Payroll toDomain(PayrollModel payrollModel) {
        var employeeModelMapper = ModelMapperFactory.getInstance().getEmployeeModelMapper();
        Payroll payroll = Payroll.builder()
                .employee(employeeModelMapper.toDomain(payrollModel.getEmployeeModel()))
                .periodType(PeriodType.valueOf(payrollModel.getPeriodType().toString()))
                .hourCount(payrollModel.getHourCount())
                .salary(new Money(
                        payrollModel.getSalary().wholePart(),
                        payrollModel.getSalary().decimalPart()))
                .paymentAt(
                        Objects.nonNull(payrollModel.getPaymentAt().atStartOfDay())
                                ? payrollModel.getPaymentAt().atStartOfDay()
                                : null)
                .build();
        payroll.setId(payrollModel.getId());
        payroll.setUpdatedAt(payrollModel.getUpdatedAt());
        payroll.setCreatedAt(payrollModel.getCreatedAt());
        return payroll;
    }

    PayrollModelMapper() {}
}
