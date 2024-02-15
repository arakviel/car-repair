package com.arakviel.carrepair.domain.mapper.impl;

import com.arakviel.carrepair.domain.impl.Money;
import com.arakviel.carrepair.domain.impl.Payroll;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.domain.mapper.PayrollMapper;
import com.arakviel.carrepair.persistence.entity.impl.PayrollEntity;

public class PayrollMapperImpl implements PayrollMapper {

    @Override
    public Payroll toDomain(PayrollEntity entity) {
        var payroll = Payroll.builder()
                .employee(MapperFactory.getInstance().getEmployeeMapper().toDomain(entity.getEmployeeEntity()))
                .periodType(Payroll.PeriodType.valueOf(entity.getPeriodType().toString()))
                .hourCount(entity.getHourCount())
                .salary(new Money(
                        entity.getSalary().wholePart(), entity.getSalary().decimalPart()))
                .paymentAt(entity.getPaymentAt())
                .build();
        payroll.setId(entity.getId());
        payroll.setUpdatedAt(entity.getUpdatedAt());
        payroll.setCreatedAt(entity.getCreatedAt());
        return payroll;
    }

    @Override
    public PayrollEntity toEntity(Payroll domain) {
        var payrollEntity = PayrollEntity.builder()
                .id(domain.getId())
                .employeeEntity(MapperFactory.getInstance().getEmployeeMapper().toEntity(domain.getEmployee()))
                .periodType(
                        PayrollEntity.PeriodType.valueOf(domain.getPeriodType().toString()))
                .hourCount(domain.getHourCount())
                .salary(new com.arakviel.carrepair.persistence.entity.impl.Money(
                        domain.getSalary().wholePart(), domain.getSalary().decimalPart()))
                .paymentAt(domain.getPaymentAt())
                .build();
        payrollEntity.setUpdatedAt(domain.getUpdatedAt());
        payrollEntity.setCreatedAt(domain.getCreatedAt());
        return payrollEntity;
    }

    private PayrollMapperImpl() {}

    private static class SingletonHolder {
        public static final PayrollMapperImpl INSTANCE = new PayrollMapperImpl();
    }

    public static PayrollMapperImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
