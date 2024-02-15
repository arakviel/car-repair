package com.arakviel.carrepair.persistence.filter.impl;

import com.arakviel.carrepair.persistence.filter.FilterDto;
import java.time.LocalDateTime;

public record ClientFilterDto(
        String phone,
        String email,
        String firstName,
        String lastName,
        String middleName,
        LocalDateTime updatedAt,
        LocalDateTime createdAt)
        implements FilterDto {}
