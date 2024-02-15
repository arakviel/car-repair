package com.arakviel.carrepair.persistence.filter.impl;

import com.arakviel.carrepair.persistence.filter.FilterDto;

public record AddressFilterDto(String country, String region, String city, String street, String home)
        implements FilterDto {}
