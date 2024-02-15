package com.arakviel.carrepair.domain.mapper.impl;

import com.arakviel.carrepair.domain.impl.Discount;
import com.arakviel.carrepair.domain.impl.Money;
import com.arakviel.carrepair.domain.impl.Order;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.domain.mapper.OrderMapper;
import com.arakviel.carrepair.persistence.entity.impl.DiscountEntity;
import com.arakviel.carrepair.persistence.entity.impl.OrderEntity;
import java.util.Objects;

public class OrderMapperImpl implements OrderMapper {

    @Override
    public Order toDomain(OrderEntity entity) {
        Discount discount = null;
        DiscountEntity discountEntity = entity.getDiscountEntity();
        if (Objects.nonNull(discountEntity)) {
            discount = MapperFactory.getInstance().getDiscountMapper().toDomain(discountEntity);
        }

        var order = Order.builder()
                .client(MapperFactory.getInstance().getClientMapper().toDomain(entity.getClientEntity()))
                .car(MapperFactory.getInstance().getCarMapper().toDomain(entity.getCarEntity()))
                .discount(discount)
                .price(new Money(
                        entity.getPrice().wholePart(), entity.getPrice().decimalPart()))
                .paymentType(Order.PaymentType.valueOf(entity.getPaymentType().toString()))
                .paymentAt(entity.getPaymentAt())
                .build();
        order.setId(entity.getId());
        order.setUpdatedAt(entity.getUpdatedAt());
        order.setCreatedAt(entity.getCreatedAt());
        return order;
    }

    @Override
    public OrderEntity toEntity(Order domain) {

        DiscountEntity discountEntity = null;
        Discount discount = domain.getDiscount();
        if (Objects.nonNull(discount)) {
            discountEntity = MapperFactory.getInstance().getDiscountMapper().toEntity(discount);
        }

        var orderEntity = OrderEntity.builder()
                .id(domain.getId())
                .clientEntity(MapperFactory.getInstance().getClientMapper().toEntity(domain.getClient()))
                .carEntity(MapperFactory.getInstance().getCarMapper().toEntity(domain.getCar()))
                .discountEntity(discountEntity)
                .price(new com.arakviel.carrepair.persistence.entity.impl.Money(
                        domain.getPrice().wholePart(), domain.getPrice().decimalPart()))
                .paymentType(
                        OrderEntity.PaymentType.valueOf(domain.getPaymentType().toString()))
                .paymentAt(domain.getPaymentAt())
                .build();
        orderEntity.setId(domain.getId());
        orderEntity.setUpdatedAt(domain.getUpdatedAt());
        orderEntity.setCreatedAt(domain.getCreatedAt());
        return orderEntity;
    }

    private OrderMapperImpl() {}

    private static class SingletonHolder {
        public static final OrderMapperImpl INSTANCE = new OrderMapperImpl();
    }

    public static OrderMapperImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
