package com.arakviel.carrepair.domain.mapper.impl;

import com.arakviel.carrepair.domain.impl.Discount;
import com.arakviel.carrepair.domain.mapper.DiscountMapper;
import com.arakviel.carrepair.persistence.entity.impl.DiscountEntity;

public class DiscountMapperImpl implements DiscountMapper {

    @Override
    public Discount toDomain(DiscountEntity entity) {
        var discount = Discount.builder()
                .name(entity.getName())
                .value(entity.getValue())
                .build();
        discount.setId(entity.getId());
        discount.setDescription(entity.getDescription());
        return discount;
    }

    @Override
    public DiscountEntity toEntity(Discount domain) {
        var discountEntity = DiscountEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .value(domain.getValue())
                .build();
        discountEntity.setDescription(domain.getDescription());
        return discountEntity;
    }

    private DiscountMapperImpl() {}

    private static class SingletonHolder {
        public static final DiscountMapperImpl INSTANCE = new DiscountMapperImpl();
    }

    public static DiscountMapperImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
