package com.arakviel.carrepair.persistence.filter.impl;

import com.arakviel.carrepair.persistence.filter.FilterDto;

public record CurrencyFilterDto(String name, String symbol) implements FilterDto {}
