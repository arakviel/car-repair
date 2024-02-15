package com.arakviel.carrepair.persistence.filter.impl;

import com.arakviel.carrepair.persistence.filter.FilterDto;

public record UserFilterDto(String email, String login) implements FilterDto {}
