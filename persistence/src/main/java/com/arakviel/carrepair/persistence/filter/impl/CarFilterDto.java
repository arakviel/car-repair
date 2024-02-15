package com.arakviel.carrepair.persistence.filter.impl;

import com.arakviel.carrepair.persistence.entity.impl.ModelEntity;
import com.arakviel.carrepair.persistence.filter.FilterDto;
import java.awt.Color;
import java.time.LocalDateTime;

public record CarFilterDto(
        ModelEntity modelEntity,
        String number,
        Short year,
        String engineType,
        Integer mileage,
        Color color,
        LocalDateTime updatedAt,
        LocalDateTime createdAt)
        implements FilterDto {}
