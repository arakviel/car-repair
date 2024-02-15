package com.arakviel.carrepair.persistence.filter.impl;

import com.arakviel.carrepair.persistence.entity.impl.BrandEntity;
import com.arakviel.carrepair.persistence.filter.FilterDto;

public record ModelFilterDto(BrandEntity brandEntity, String name) implements FilterDto {}
