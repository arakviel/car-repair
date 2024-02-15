package com.arakviel.carrepair.presentation.mapper.impl;

import com.arakviel.carrepair.domain.impl.Discount;
import com.arakviel.carrepair.presentation.mapper.ModelMapper;
import com.arakviel.carrepair.presentation.model.impl.DiscountModel;

public class DiscountModelMapper implements ModelMapper<Discount, DiscountModel> {

    @Override
    public DiscountModel toModel(Discount discount) {
        DiscountModel discountModel = DiscountModel.builder()
                .name(discount.getName())
                .value(discount.getValue())
                .build();
        discountModel.setId(discount.getId());
        discountModel.setDescription(discount.getDescription());
        return discountModel;
    }

    @Override
    public Discount toDomain(DiscountModel discountModel) {
        Discount discount = Discount.builder()
                .name(discountModel.getName())
                .value((short) discountModel.getValue())
                .build();
        discount.setId(discountModel.getId());
        discount.setDescription(discountModel.getDescription());
        return discount;
    }
}
