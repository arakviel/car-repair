package com.arakviel.carrepair.persistence.filter.impl;

import com.arakviel.carrepair.persistence.filter.FilterDto;

public record DiscountFilterDto(String name, short value) implements FilterDto {}
