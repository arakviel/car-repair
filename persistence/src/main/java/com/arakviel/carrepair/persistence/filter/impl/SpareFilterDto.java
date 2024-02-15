package com.arakviel.carrepair.persistence.filter.impl;

import com.arakviel.carrepair.persistence.entity.impl.Money;
import com.arakviel.carrepair.persistence.entity.impl.WorkroomEntity;
import com.arakviel.carrepair.persistence.filter.FilterDto;

public record SpareFilterDto(WorkroomEntity workroomEntity, String name, Money price, Integer quantityInStock)
        implements FilterDto {}
