package com.arakviel.carrepair.persistence.filter.impl;

import com.arakviel.carrepair.persistence.filter.FilterDto;

public record RoleFilterDto(
        Boolean canEditUsers,
        Boolean canEditSpares,
        Boolean canEditClients,
        Boolean canEditServices,
        Boolean canEditOrders,
        Boolean canEditPayrolls)
        implements FilterDto {}
