package com.arakviel.carrepair.persistence.filter.impl;

import com.arakviel.carrepair.persistence.entity.impl.EmployeeEntity;
import com.arakviel.carrepair.persistence.entity.impl.Money;
import com.arakviel.carrepair.persistence.filter.FilterDto;
import java.time.LocalDateTime;

public record PayrollFilterDto(
        EmployeeEntity employeeEntity,
        String periodType,
        Integer hourCount,
        Money salary,
        LocalDateTime paymentAt,
        LocalDateTime updatedAt,
        LocalDateTime createdAt)
        implements FilterDto {}
