package com.arakviel.carrepair.persistence.filter.impl;

import com.arakviel.carrepair.persistence.entity.impl.EmployeeEntity;
import com.arakviel.carrepair.persistence.filter.FilterDto;

public record PhoneFilterDto(EmployeeEntity employeeEntity, String type, String value) implements
        FilterDto {}
