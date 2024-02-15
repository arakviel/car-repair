package com.arakviel.carrepair.persistence.filter.impl;

import com.arakviel.carrepair.persistence.entity.impl.AddressEntity;
import com.arakviel.carrepair.persistence.filter.FilterDto;

public record WorkroomFilterDto(AddressEntity addressEntity, String name) implements FilterDto {}
