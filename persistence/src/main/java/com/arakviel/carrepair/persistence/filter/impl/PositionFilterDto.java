package com.arakviel.carrepair.persistence.filter.impl;

import com.arakviel.carrepair.persistence.entity.impl.CurrencyEntity;
import com.arakviel.carrepair.persistence.entity.impl.Money;
import com.arakviel.carrepair.persistence.filter.FilterDto;

public record PositionFilterDto(String name, CurrencyEntity currencyEntity, Money salary) implements
        FilterDto {}
