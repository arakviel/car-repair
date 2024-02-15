package com.arakviel.carrepair.persistence.filter.impl;

import com.arakviel.carrepair.persistence.entity.impl.CarEntity;
import com.arakviel.carrepair.persistence.entity.impl.ClientEntity;
import com.arakviel.carrepair.persistence.entity.impl.DiscountEntity;
import com.arakviel.carrepair.persistence.entity.impl.Money;
import com.arakviel.carrepair.persistence.filter.FilterDto;
import java.time.LocalDateTime;

public record OrderFilterDto(
        ClientEntity clientEntity,
        CarEntity carEntity,
        DiscountEntity discountEntity,
        Money price,
        String paymentType,
        LocalDateTime paymentAt,
        LocalDateTime updatedAt,
        LocalDateTime createdAt)
        implements FilterDto {}
