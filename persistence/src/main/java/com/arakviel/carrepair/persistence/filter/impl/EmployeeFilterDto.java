package com.arakviel.carrepair.persistence.filter.impl;

import com.arakviel.carrepair.persistence.entity.impl.AddressEntity;
import com.arakviel.carrepair.persistence.entity.impl.PositionEntity;
import com.arakviel.carrepair.persistence.entity.impl.WorkroomEntity;
import com.arakviel.carrepair.persistence.filter.FilterDto;
import java.time.LocalDateTime;

public record EmployeeFilterDto(
        AddressEntity addressEntity,
        WorkroomEntity workroomEntity,
        PositionEntity positionEntity,
        String firstName,
        String lastName,
        String middleName,
        LocalDateTime updatedAt,
        LocalDateTime createdAt)
        implements FilterDto {}
